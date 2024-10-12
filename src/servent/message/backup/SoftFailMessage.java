package servent.message.backup;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class SoftFailMessage extends BasicMessage {
    public SoftFailMessage(int senderPort, int receiverPort) {
        super(MessageType.IS_ALIVE , senderPort, receiverPort);
    }
}
