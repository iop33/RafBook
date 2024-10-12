package servent.handler.files;

import app.AppConfig;
import app.mutex.MutexState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.TellPortFilesMessage;
import servent.message.util.MessageUtil;

public class GetFilesOnPortHandler implements MessageHandler {
    
    private Message clientMessage;
    
    public GetFilesOnPortHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    
    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.GET_PORT_FILES) {
            AppConfig.timestampedStandardPrint(
                    "Get files handler got message from " + clientMessage.getSenderPort()
                            + ": GET_PORT_FILES " + AppConfig.chordState.getFilesIAdded());
            synchronized (MutexState.lock) {
                AppConfig.timestampedStandardPrint(
                        "Sending files to " + clientMessage.getSenderPort() + ": FILES: "
                                + AppConfig.chordState.getFilesIAdded());
                
                TellPortFilesMessage tellPortFilesMessage =
                        new TellPortFilesMessage(AppConfig.myServentInfo.getListenerPort(),
                                clientMessage.getSenderPort(),
                                AppConfig.chordState.getFilesIAdded());
                MessageUtil.sendMessage(tellPortFilesMessage);
            }
        } else {
            AppConfig.timestampedErrorPrint(
                    "Get files handler got message that is not get files on port");
        }
    }
}
