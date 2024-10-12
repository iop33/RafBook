package servent.message;

public class RemoveNodeMessage extends BasicMessage {
    public RemoveNodeMessage(int senderPort, int receiverPort, int failedNodePort) {
        super(MessageType.REMOVE_NODE, senderPort, receiverPort, String.valueOf(failedNodePort));
    }
}
