package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class RemoveFileMessage extends BasicMessage {
    public RemoveFileMessage(int senderPort, int receiverPort, String fileName) {
        super(MessageType.REMOVE_FILE, senderPort, receiverPort, fileName);
    }
}
