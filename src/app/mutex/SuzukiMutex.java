package app.mutex;

import app.AppConfig;
import app.ServentInfo;
import app.backup.Buddy;
import servent.message.suzuki.SuzukiRequestMessage;
import servent.message.util.MessageUtil;

public class SuzukiMutex implements DistributedMutex {

    @Override
    public void lock() {
        // Request a lock by either acquiring the token or waiting for it
        AppConfig.timestampedStandardPrint("Need Lock " + AppConfig.mutexState.toString());
        if (AppConfig.mutexState.getSuzukiToken() == null) {
            // Increment the request number and broadcast the request
            int sn = AppConfig.mutexState.incrementRN();
            broadcastRequest(AppConfig.myServentInfo.getListenerPort(), sn);
            // Wait until the token arrives
            waitForToken();
            AppConfig.timestampedStandardPrint("- - - Locked - - -");
            AppConfig.mutexState.enterCriticalSection();
            return;
        }
        waitForCriticalSectionToFinish();
        AppConfig.timestampedStandardPrint("- - - Locked - - -");
        AppConfig.mutexState.enterCriticalSection();
    }

    private void waitForCriticalSectionToFinish() {
        // Wait for the critical section to finish
        AppConfig.timestampedStandardPrint("Waiting for Critical Section to finish...");
        while (AppConfig.mutexState.isInCriticalSection()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void waitForToken() {
        // Wait for the token to arrive
        AppConfig.timestampedStandardPrint("Waiting for token to arrive...");
        // If Buddy is stopped, the node should also stop waiting for the token
        while (!AppConfig.mutexState.HasToken() && Buddy.getInstance().isWorking()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void unlock() {
        // Release the lock and update the token
        AppConfig.mutexState.exitCriticalSection();
        AppConfig.timestampedStandardPrint("- - - Unlocked - - -");
        AppConfig.mutexState.updateOwnLN();
        AppConfig.mutexState.updateTokenQueue();
        // Wait for all waiting threads to finish
        while (AppConfig.mutexState.getWaitingThreads().get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Send the token to the next node in the queue
        int topOfQueue = AppConfig.mutexState.getSuzukiToken().getFirstFromQueue();
        if (topOfQueue == -1) // Queue is empty
            return;
        AppConfig.mutexState.sendToken(topOfQueue);
    }

    private void broadcastRequest(int senderPort, int sn) {
        // Broadcast the token request to all nodes
        AppConfig.timestampedStandardPrint("Broadcasting Token Request Message");
        for (ServentInfo servent : AppConfig.chordState.getAllNodeInfo()) {
            if (servent.getListenerPort() == AppConfig.myServentInfo.getListenerPort())
                continue; // Skip myself
            SuzukiRequestMessage message = new SuzukiRequestMessage(senderPort, servent.getListenerPort(), sn);
            MessageUtil.sendMessage(message);
        }
    }
}
