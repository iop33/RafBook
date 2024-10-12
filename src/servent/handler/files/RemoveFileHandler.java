package servent.handler.files;

import app.AppConfig;
import app.mutex.MutexState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.RemoveFileMessage;

public class RemoveFileHandler implements MessageHandler {

    private RemoveFileMessage clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = (RemoveFileMessage) clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE_FILE) {
            String filePath = clientMessage.getMessageText();
            if (!filePath.isEmpty()) {
                try {
                    synchronized (MutexState.lock) {
                        // Remove the file from the chord state
                        AppConfig.chordState.removeFileWithPath(filePath);
                    }
                } catch (NumberFormatException e) {
                    AppConfig.timestampedErrorPrint("Got remove file message with bad text: " + clientMessage.getMessageText());
                }
            } else {
                AppConfig.timestampedErrorPrint("Got remove file message with bad text: " + clientMessage.getMessageText());
            }
        } else {
            AppConfig.timestampedErrorPrint("Remove file handler got a message that is not REMOVE_FILE");
        }
    }
}
