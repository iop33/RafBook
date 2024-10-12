package servent.handler.suzuki;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.suzuki.SuzukiGiveTokenMessage;

public class SuzukiGiveTokenHandler implements MessageHandler {
    private SuzukiGiveTokenMessage clientMessage;
    public SuzukiGiveTokenHandler(Message clientMessage){
        this.clientMessage = (SuzukiGiveTokenMessage) clientMessage;
    }
    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("Received token from " + clientMessage.getSenderPort());
        AppConfig.mutexState.setSuzukiToken(clientMessage.getSuzukiToken());
    }
}
