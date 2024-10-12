package servent.message.suzuki;

import app.mutex.SuzukiToken;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class SuzukiGiveTokenMessage extends BasicMessage {
    private SuzukiToken suzukiToken;

    public SuzukiGiveTokenMessage(int senderPort, int receiverPort, SuzukiToken suzukiToken) {
        super(MessageType.SUZUKI_GIVE_TOKEN, senderPort, receiverPort);
        this.suzukiToken = suzukiToken;
    }

    public SuzukiToken getSuzukiToken() {
        return suzukiToken;
    }
}
