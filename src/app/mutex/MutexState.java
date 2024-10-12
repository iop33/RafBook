package app.mutex;

import app.AppConfig;
import servent.message.suzuki.SuzukiGiveTokenMessage;
import servent.message.util.MessageUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MutexState {

    private volatile boolean hasToken = false; // Flag indicating if the node has the token
    private volatile boolean inCriticalSection = false; // Flag indicating if the node is in a critical section
    private SuzukiToken suzukiToken = null; // The current Suzuki token held by the node
    private Map<Integer, Integer> RN = new ConcurrentHashMap<>(); // Map of request numbers for each node
    public static final Object lock = new Object(); // Lock object for synchronization
    private AtomicInteger waitingThreads = new AtomicInteger(0); // Counter for threads waiting to enter the critical section
    public static volatile AtomicBoolean isReorganizationDone = new AtomicBoolean(false); // Flag for reorganization status

    // Getter for the Suzuki token
    public SuzukiToken getSuzukiToken() {
        return suzukiToken;
    }

    // Checks if the node has the token
    public boolean HasToken() {
        return hasToken;
    }

    // Checks if the node is in the critical section
    public boolean isInCriticalSection() {
        return inCriticalSection;
    }

    // Method to enter the critical section
    public void enterCriticalSection() {
        if (suzukiToken == null || !hasToken) {
            AppConfig.timestampedErrorPrint("Can't enter Critical Section: I don't have token");
            return;
        }
        AppConfig.timestampedStandardPrint("Inside Critical Section");
        inCriticalSection = true;
    }

    // Method to exit the critical section
    public void exitCriticalSection() {
        if (suzukiToken == null || !hasToken) {
            AppConfig.timestampedErrorPrint("Can't exit Critical Section: I don't have token");
            return;
        }
        inCriticalSection = false;
    }

    // Method to set the Suzuki token
    public void setSuzukiToken(SuzukiToken suzukiToken) {
        this.suzukiToken = suzukiToken;
        hasToken = true;
    }

    // Method to remove the Suzuki token
    public void removeSuzukiToken() {
        if(inCriticalSection){
            AppConfig.timestampedErrorPrint("Trying to remove token while in Critical Section");
        }
        suzukiToken = null;
        hasToken = false;
    }

    // Method to increment the request number (RN) for the current node
    public int incrementRN() {
        int oldVal = RN.getOrDefault(AppConfig.myServentInfo.getListenerPort(), 0);
        int sn = oldVal + 1;
        RN.put(AppConfig.myServentInfo.getListenerPort(), sn);
        return sn;
    }

    // Method to update the RN value for a given sender port to the maximum value
    public int updateRNMaxToMaxValue(int senderPort, int sn) {
        int oldVal = RN.getOrDefault(senderPort, 0);
        int maxVal = Math.max(oldVal, sn);
        RN.put(senderPort, maxVal);
        return maxVal;
    }

    // Method to update the LN value for the current node
    public void updateOwnLN(){
        int myPort = AppConfig.myServentInfo.getListenerPort();
        suzukiToken.getLN().put(myPort, RN.getOrDefault(myPort, 0));
    }

    // Method to update the token queue based on RN and LN values
    public void updateTokenQueue(){
        Map<Integer, Integer> LN = suzukiToken.getLN();
        for (Map.Entry<Integer, Integer> entry : RN.entrySet()){
            int key = entry.getKey();
            int rnValue = entry.getValue();
            if(key == AppConfig.myServentInfo.getListenerPort())
                continue; // Skip myself
            if(!suzukiToken.getQueue().contains(key) && rnValue == LN.getOrDefault(key, 0) + 1){
                suzukiToken.addToQueue(key);
                AppConfig.timestampedStandardPrint("Added to token queue: " + key);
            } else {
                AppConfig.timestampedStandardPrint("Not added to token queue: " + key + ", because RN != LN + 1");
            }
        }
    }

    // Method to send the token to another node
    public void sendToken(int receiverPort){
        SuzukiToken copiedToken = suzukiToken.getCopy();
        removeSuzukiToken();
        SuzukiGiveTokenMessage message = new SuzukiGiveTokenMessage(AppConfig.myServentInfo.getListenerPort(), receiverPort, copiedToken);
        MessageUtil.sendMessage(message);
    }

    // Method to increment the count of threads waiting for the lock
    public void threadWaitingLock(){
        waitingThreads.incrementAndGet();
    }

    // Method to decrement the count of threads waiting for the lock
    public void threadInsideLock(){
        waitingThreads.decrementAndGet();
    }

    // Getter for the waitingThreads counter
    public AtomicInteger getWaitingThreads() {
        return waitingThreads;
    }

    // toString method to return a string representation of the mutex state
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MutexState {\n");
        sb.append("  hasToken: ").append(hasToken).append(",\n");
        sb.append("  inCriticalSection: ").append(inCriticalSection).append(",\n");
        sb.append("  RN: {");
        for (Map.Entry<Integer, Integer> entry : RN.entrySet()) {
            sb.append("Port ").append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        sb.append("},\n");
        if (suzukiToken != null) {
            sb.append("  Token: ").append(suzukiToken.toString()).append("\n");
        } else {
            sb.append("  Token: null\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
