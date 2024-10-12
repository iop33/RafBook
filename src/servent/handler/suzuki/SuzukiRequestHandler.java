package servent.handler.suzuki;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.suzuki.SuzukiRequestMessage;

import java.util.Map;

public class SuzukiRequestHandler implements MessageHandler {
    private SuzukiRequestMessage clientMessage;
    public SuzukiRequestHandler(Message clientMessage){
        this.clientMessage = (SuzukiRequestMessage) clientMessage;
    }
    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("Token requested from " + clientMessage.getSenderPort() + ", " + AppConfig.mutexState.toString());
        int newRN = AppConfig.mutexState.updateRNMaxToMaxValue(clientMessage.getSenderPort(), clientMessage.getSn());
        // If I have token and I don't use it
        if(AppConfig.mutexState.HasToken() && !AppConfig.mutexState.isInCriticalSection()) {
            Map<Integer, Integer> LN = AppConfig.mutexState.getSuzukiToken().getLN();
            int lnVal = LN.getOrDefault(clientMessage.getSenderPort(), 0);
            if(newRN == lnVal + 1){
                AppConfig.mutexState.sendToken(clientMessage.getSenderPort());
            }
            else {
                AppConfig.timestampedStandardPrint("Can't send token to " + clientMessage.getSenderPort() + ", because RN != LN + 1");
            }
        }
    }
}
