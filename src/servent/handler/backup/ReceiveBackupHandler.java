package servent.handler.backup;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.backup.SendBackupMessage;

public class ReceiveBackupHandler implements MessageHandler {
    private SendBackupMessage clientMessage;

    public ReceiveBackupHandler(Message clientMessage){
        this.clientMessage = (SendBackupMessage) clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.SEND_BACKUP){
            AppConfig.chordState.addBackup(clientMessage.getBackup());
        }
    }
}
