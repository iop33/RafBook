package app.backup;

import app.AppConfig;
import app.ServentInfo;
import servent.message.backup.SendBackupMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class Backuper implements Runnable{
    private static final int BACKUP_INTERVAL = 5000; // 5s
    private volatile boolean working = true;
    @Override
    public void run() {
        while(working){
            try {
                Thread.sleep(BACKUP_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ServentInfo successor = AppConfig.chordState.getSuccessor();
            if(successor == null)
                continue;
            int successorPort = successor.getListenerPort();
            Map<Integer, DistributedFile> backup = AppConfig.chordState.getValueMap();
            SendBackupMessage message = new SendBackupMessage(AppConfig.myServentInfo.getListenerPort(), successorPort, backup);
            MessageUtil.sendMessage(message);
        }
    }

    public void stop(){
        working = false;
    }
}
