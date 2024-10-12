package servent.message.backup;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class PingMessage extends BasicMessage {
    public PingMessage(int senderPort, int receiverPort) {
        super(MessageType.PING, senderPort, receiverPort);
    }
}
