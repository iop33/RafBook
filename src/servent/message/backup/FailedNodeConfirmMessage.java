package servent.message.backup;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class FailedNodeConfirmMessage extends BasicMessage {
    public FailedNodeConfirmMessage(int senderPort, int receiverPort, int failedNodePort) {
        super(MessageType.FAILED_NODE_CONFIRM, senderPort, receiverPort, String.valueOf(failedNodePort));
    }
}
