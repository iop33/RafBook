package servent.handler.files;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.FileRemovedMessage;

public class FileRemovedHandler implements MessageHandler {

    private Message clientMessage;

    public FileRemovedHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.FILE_REMOVED) {
            int key = ((FileRemovedMessage)clientMessage).getKey();
            AppConfig.chordState.removeFilesFromFilesIAdded(key);
            AppConfig.timestampedStandardPrint("I removed file from files i added");
        } else {
            AppConfig.timestampedErrorPrint("Got message that is not FILE_REMOVED");
        }
    }
}
