package servent.message.friends;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class NoPermissionMessage extends BasicMessage {
    public NoPermissionMessage(int senderPort, int receiverPort, int key) {
        super(MessageType.NO_PERMISSION, senderPort, receiverPort, String.valueOf(key));
    }
}
