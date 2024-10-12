package servent.handler.files;

import app.AppConfig;
import app.backup.DistributedFile;
import app.mutex.MutexState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.AddFileMessage;

public class AddFileHandler implements MessageHandler {

	private Message clientMessage;

	public AddFileHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.ADD_FILE) {
			String keyString = clientMessage.getMessageText();
			if (!keyString.isEmpty()) {
				int key = 0;
				DistributedFile value = null;

				try {
					AddFileMessage addFileMessage = (AddFileMessage) clientMessage;
					key = Integer.parseInt(keyString);
					value = addFileMessage.getDistributedFile();
					synchronized (MutexState.lock) {
						// Store the value in the chord state
						AppConfig.chordState.putValue(key, value);
					}
				} catch (NumberFormatException e) {
					AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
				}
			} else {
				AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
			}
		} else {
			AppConfig.timestampedErrorPrint("Put handler got a message that is not PUT");
		}
	}
}
