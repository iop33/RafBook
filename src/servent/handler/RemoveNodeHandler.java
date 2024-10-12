package servent.handler;

import app.AppConfig;
import app.mutex.MutexState;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveNodeMessage;
import servent.message.util.MessageUtil;

public class RemoveNodeHandler implements MessageHandler{
    private Message clientMessage;

    public RemoveNodeHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.REMOVE_NODE){
            if(clientMessage.getSenderPort() == AppConfig.myServentInfo.getListenerPort()){
                AppConfig.timestampedStandardPrint("Removed node status updated on all nodes");
                return;
            }
            int failedNodePort = Integer.parseInt(clientMessage.getMessageText());
            synchronized (MutexState.lock){
                AppConfig.chordState.handleNodeFailure(failedNodePort);
                AppConfig.timestampedStandardPrint("Failed node with port " + failedNodePort + " removed, sending message to successor");
                AppConfig.timestampedStandardPrint("Sending remove message to " + AppConfig.chordState.getNextNodePort());
                RemoveNodeMessage removeNodeMessage = new RemoveNodeMessage(clientMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(), failedNodePort);
                MessageUtil.sendMessage(removeNodeMessage);
            }
        }
    }
}
