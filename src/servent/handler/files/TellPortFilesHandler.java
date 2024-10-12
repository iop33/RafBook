package servent.handler.files;

import app.AppConfig;
import app.backup.DistributedFile;
import app.mutex.MutexState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.TellPortFilesMessage;

import java.util.List;

public class TellPortFilesHandler implements MessageHandler {
    
    private Message clientMessage;
    
    public TellPortFilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }
    
    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.TELL_PORT_FILES) {
            List<Integer> fileIds = ((TellPortFilesMessage) clientMessage).getFileIds();
            synchronized (MutexState.lock) {
                for (Integer fileId : fileIds) {
                    DistributedFile file = AppConfig.chordState.getFileValue(fileId);
                    AppConfig.timestampedStandardPrint("[File Content]:\n" + file.getContent());
                }
            }
        } else {
            AppConfig.timestampedErrorPrint("Got message thet is not TELL_PORT_FILES");
        }
    }
}
