package servent.handler.friends;

import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class NoPermissionHandler implements MessageHandler {
    private Message clientMessage;
    public NoPermissionHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType().equals(MessageType.NO_PERMISSION)){
            System.out.println("You don't have permission to view file with key " + clientMessage.getMessageText());
        }
    }
}
