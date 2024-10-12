package servent.handler.backup;

import app.backup.Buddy;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class FailedNodeConfirmHandler implements MessageHandler {
    private Message clientMessage;

    public FailedNodeConfirmHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.FAILED_NODE_CONFIRM){
            int failedNodePort = Integer.parseInt(clientMessage.getMessageText());
            Buddy.getInstance().addFailedNodeConfirm(clientMessage.getSenderPort(), failedNodePort);
        }
    }
}
