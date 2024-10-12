package servent.message.organization;

import app.AppConfig;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class DoneReorganizingMessage extends BasicMessage {
    public DoneReorganizingMessage(int senderPort, int receiverPort) {
        super(MessageType.DONE_REORGANIZING, senderPort, receiverPort);
    }
}
