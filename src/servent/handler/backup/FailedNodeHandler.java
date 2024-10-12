package servent.handler.backup;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.backup.FailedNodeConfirmMessage;
import servent.message.backup.FailedNodeMessage;
import servent.message.util.MessageUtil;

public class FailedNodeHandler implements MessageHandler {

    private FailedNodeMessage clintMessage;
    public FailedNodeHandler(Message clientMessage){
        this.clintMessage = (FailedNodeMessage) clientMessage;
    }
    @Override
    public void run() {
        if(clintMessage.getMessageType() == MessageType.FAILED_NODE){
            int failedNodePort = Integer.parseInt(clintMessage.getMessageText());
            AppConfig.chordState.addFailedNode(failedNodePort);
            FailedNodeConfirmMessage message = new FailedNodeConfirmMessage(AppConfig.myServentInfo.getListenerPort(), clintMessage.getSenderPort(), failedNodePort);
            MessageUtil.sendMessage(message);
        }
    }
}
