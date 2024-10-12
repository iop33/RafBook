package servent.handler.organization;

import app.AppConfig;
import app.mutex.MutexState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class DoneReorganizingHandler implements MessageHandler {
    private Message clientMessage;
    public DoneReorganizingHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.DONE_REORGANIZING){
            MutexState.isReorganizationDone.set(true);
            AppConfig.timestampedStandardPrint("Reorganization done, message received from: " + clientMessage.getSenderPort());
        }
    }
}
