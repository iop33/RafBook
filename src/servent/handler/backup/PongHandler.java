package servent.handler.backup;

import app.backup.Buddy;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class PongHandler implements MessageHandler {
    private Message clientMessage;
    public PongHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.PONG){
            Buddy.getInstance().buddyPonged(clientMessage.getSenderPort());
        }
    }
}
