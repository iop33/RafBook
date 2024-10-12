package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FileRemovedMessage extends BasicMessage {

    private Integer key;
    public FileRemovedMessage(int senderPort, int receiverPort, int key) {
        super(MessageType.FILE_REMOVED, senderPort, receiverPort);
    }

    public Integer getKey() {
        return key;
    }
}
