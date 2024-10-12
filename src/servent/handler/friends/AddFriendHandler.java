package servent.handler.friends;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;

public class AddFriendHandler implements MessageHandler {
    private Message clientMessage;
    public AddFriendHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.ADD_FRIEND){
            AppConfig.chordState.addFriend(clientMessage.getSenderPort());
        }
    }
}
