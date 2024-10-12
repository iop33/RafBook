package servent.message.backup;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FailedNodeMessage extends BasicMessage {
    public FailedNodeMessage(int senderPort, int receiverPort, int failedNodePort) {
        super(MessageType.FAILED_NODE, senderPort, receiverPort, String.valueOf(failedNodePort));
    }
}
