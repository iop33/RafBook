package servent.handler.backup;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.backup.PongMessage;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {
    private Message clientMessage;
    public PingHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.PING){
            PongMessage pongMessage = new PongMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
            MessageUtil.sendMessage(pongMessage);
        }
    }
}
