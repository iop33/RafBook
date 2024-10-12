package servent.message.suzuki;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class SuzukiRequestMessage extends BasicMessage {
    private int sn;
    public SuzukiRequestMessage(int senderPort, int receiverPort, int sn) {
        super(MessageType.SUZUKI_REQUEST, senderPort, receiverPort);
        this.sn = sn;
    }

    public int getSn() {
        return sn;
    }
}
