package app.backup;

import app.AppConfig;
import app.ServentInfo;
import app.mutex.DistributedMutex;
import app.mutex.MutexState;
import servent.message.RemoveNodeMessage;
import servent.message.backup.FailedNodeMessage;
import servent.message.backup.PingMessage;
import servent.message.backup.SoftFailMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Buddy implements Runnable {

    private static final int PING_INTERVAL = 1000; // 1s
    private static final int SOFT_FAIL_TIME = 6000; // 6s
    private static final int HARD_FAIL_TIME = 11000; // 11s

    private volatile boolean pongReceivedFromBuddy1 = false;
    private volatile boolean pongReceivedFromBuddy2 = false;
    private volatile boolean isActive = true;

    private Map<Integer, Set<Integer>> nodeFailConfirmationMap = new ConcurrentHashMap<>();
    private DistributedMutex distributedMutex;

    private Buddy() {}

    private static Buddy singletonInstance;

    public static Buddy getInstance(){
        if(singletonInstance == null){
            singletonInstance = new Buddy();
        }
        return singletonInstance;
    }

    @Override
    public void run() {
        while(isActive){
            performPingInterval();

            ServentInfo predecessor = AppConfig.chordState.getPredecessor();
            ServentInfo successor = AppConfig.chordState.getSuccessor();

            if(predecessor == null || successor == null)
                continue;

            AppConfig.timestampedStandardPrint("Pinging buddies: " + predecessor + ", " + successor);

            sendPingMessages(predecessor, successor);

            waitForSoftFailDetection();

            if(pongReceivedFromBuddy1 && pongReceivedFromBuddy2){
                AppConfig.timestampedStandardPrint("Both buddies responded with pong");
                continue;
            }

            handleSoftFail(predecessor, successor);

            waitForHardFailDetection();

            if(pongReceivedFromBuddy1 && pongReceivedFromBuddy2){
                AppConfig.timestampedStandardPrint("Both buddies responded with pong");
                continue;
            }

            if(!isActive)
                return;

            handleHardFail(predecessor, successor);
        }
    }

    private void performPingInterval() {
        try {
            Thread.sleep(PING_INTERVAL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        pongReceivedFromBuddy1 = false;
        pongReceivedFromBuddy2 = false;
    }

    private void sendPingMessages(ServentInfo buddy1, ServentInfo buddy2) {
        PingMessage pingMsg1 = new PingMessage(AppConfig.myServentInfo.getListenerPort(), buddy1.getListenerPort());
        PingMessage pingMsg2 = new PingMessage(AppConfig.myServentInfo.getListenerPort(), buddy2.getListenerPort());

        if(buddy1 != AppConfig.myServentInfo){
            MessageUtil.sendMessage(pingMsg1);
        } else {
            AppConfig.timestampedStandardPrint("Skipping ping to myself");
            pongReceivedFromBuddy1 = true;
        }
        if(buddy2 != AppConfig.myServentInfo){
            MessageUtil.sendMessage(pingMsg2);
        } else {
            AppConfig.timestampedStandardPrint("Skipping ping to myself");
            pongReceivedFromBuddy2 = true;
        }
    }

    private void waitForSoftFailDetection() {
        try {
            Thread.sleep(SOFT_FAIL_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void handleSoftFail(ServentInfo buddy1, ServentInfo buddy2) {
        if(!pongReceivedFromBuddy1){
            handleSoftFailForBuddy(buddy1);
        }
        if(!pongReceivedFromBuddy2){
            handleSoftFailForBuddy(buddy2);
        }
    }

    private void handleSoftFailForBuddy(ServentInfo buddy) {
        AppConfig.timestampedStandardPrint(buddy.getChordId() + " with port " + buddy.getListenerPort() + " SOFT FAILED");
        if(buddy == AppConfig.chordState.getPredecessor() || buddy == AppConfig.chordState.getSuccessor()){
            SoftFailMessage softFailMsg = new SoftFailMessage(AppConfig.myServentInfo.getListenerPort(), buddy.getListenerPort());
            MessageUtil.sendMessage(softFailMsg);
        }
    }

    private void waitForHardFailDetection() {
        try {
            Thread.sleep(HARD_FAIL_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void handleHardFail(ServentInfo buddy1, ServentInfo buddy2) {
        if(!pongReceivedFromBuddy1 && buddy1.getListenerPort() == AppConfig.chordState.getPredecessor().getListenerPort()){
            processHardFail(buddy1);
        }
        if(!pongReceivedFromBuddy2 && buddy2.getListenerPort() == AppConfig.chordState.getSuccessor().getListenerPort()){
            processHardFail(buddy2);
        }
    }

    private void processHardFail(ServentInfo buddy) {
        AppConfig.timestampedStandardPrint(buddy.getChordId() + " with port " + buddy.getListenerPort() + " HARD FAILED");
        broadcastFailedNodeMessage(buddy.getListenerPort());
        if(buddy == AppConfig.chordState.getPredecessor()){
            handleNodeFailureWithMutex(buddy);
        }
    }

    private void handleNodeFailureWithMutex(ServentInfo buddy) {
        AppConfig.mutexState.threadWaitingLock();
        distributedMutex.lock();
        AppConfig.mutexState.threadInsideLock();
        synchronized (MutexState.lock){
            AppConfig.timestampedStandardPrint("Handling node failure");
            AppConfig.chordState.handleNodeFailure(buddy.getListenerPort());
            notifyBootstrapToRemoveNode(buddy.getListenerPort());
            AppConfig.timestampedStandardPrint("Initiating removal of [" + buddy.getListenerPort() + "]: Sending remove message to " + AppConfig.chordState.getNextNodePort());
            broadcastRemoveNodeMessage(buddy.getListenerPort());
        }
        awaitNodeRemovalConfirmation(buddy.getListenerPort());
        AppConfig.timestampedStandardPrint("All nodes confirmed removal of failed node!");
        distributedMutex.unlock();
    }

    private void awaitNodeRemovalConfirmation(int failedNodePort){
        AppConfig.timestampedStandardPrint("Waiting for confirmation of failed node removal");
        while(isActive){
            Set<Integer> failedNodes = AppConfig.chordState.getFailedNodes();
            Queue<Integer> allNodes = new ArrayDeque<>();
            for (ServentInfo node : AppConfig.chordState.getAllNodeInfo()){
                allNodes.offer(node.getListenerPort());
            }
            allNodes.removeAll(nodeFailConfirmationMap.getOrDefault(failedNodePort, new HashSet<>()));
            allNodes.removeAll(failedNodes);
            if(allNodes.isEmpty())
                return;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private void broadcastFailedNodeMessage(int failedNodePort){
        for (ServentInfo node : AppConfig.chordState.getAllNodeInfo()){
            FailedNodeMessage failedNodeMsg = new FailedNodeMessage(AppConfig.myServentInfo.getListenerPort(), node.getListenerPort(), failedNodePort);
            MessageUtil.sendMessage(failedNodeMsg);
        }
    }

    private void broadcastRemoveNodeMessage(int failedNodePort){
        for (ServentInfo node : AppConfig.chordState.getAllNodeInfo()){
            RemoveNodeMessage removeNodeMsg = new RemoveNodeMessage(AppConfig.myServentInfo.getListenerPort(), node.getListenerPort(), failedNodePort);
            MessageUtil.sendMessage(removeNodeMsg);
        }
    }

    public void buddyPonged(int buddyPort){
        AppConfig.timestampedStandardPrint("Buddy " + buddyPort + " responded with pong");
        if(buddyPort == AppConfig.chordState.getPredecessor().getListenerPort()){
            pongReceivedFromBuddy1 = true;
        }
        if(buddyPort == AppConfig.chordState.getSuccessor().getListenerPort()){
            pongReceivedFromBuddy2 = true;
        }
    }

    public void stop(){
        isActive = false;
    }

    public void setMutex(DistributedMutex mutex) {
        this.distributedMutex = mutex;
    }

    public void addFailedNodeConfirm(int senderPort, int failedNodePort) {
        AppConfig.timestampedStandardPrint("Node " + senderPort + " confirmed failure of node: " + failedNodePort);
        nodeFailConfirmationMap.compute(failedNodePort, (key, existingSet) -> {
            if (existingSet == null) {
                existingSet = new HashSet<>();
            }
            existingSet.add(senderPort);
            return existingSet;
        });
    }

    private void notifyBootstrapToRemoveNode(int port){
        int bootstrapPort = AppConfig.BOOTSTRAP_PORT;

        try {
            Socket bootstrapSocket = new Socket("localhost", bootstrapPort);

            PrintWriter bootstrapWriter = new PrintWriter(bootstrapSocket.getOutputStream());
            bootstrapWriter.write("Remove " + port);
            bootstrapWriter.flush();

            AppConfig.timestampedStandardPrint("Node removed from bootstrap...");
            bootstrapSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isWorking() {
        return isActive;
    }
}
