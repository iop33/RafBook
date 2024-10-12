package servent.message.friends;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class AddFriendMessage extends BasicMessage {
    public AddFriendMessage(int senderPort, int receiverPort) {
        super(MessageType.ADD_FRIEND, senderPort, receiverPort);
    }
}
