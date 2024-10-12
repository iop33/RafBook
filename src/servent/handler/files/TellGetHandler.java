package servent.handler.files;

import app.AppConfig;
import app.backup.DistributedFile;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.files.TellGetMessage;

public class TellGetHandler implements MessageHandler {

	private Message clientMessage;

	public TellGetHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.TELL_GET) {
			if (!clientMessage.getMessageText().isEmpty()) {
				try {
					int key = Integer.parseInt(clientMessage.getMessageText());
					TellGetMessage tellGetMessage = (TellGetMessage) clientMessage;
					DistributedFile value = tellGetMessage.getDistributedFile();
					if (value == null) {
						AppConfig.timestampedStandardPrint("From: " + clientMessage.getSenderPort() + " - No such key: " + key);
					} else {
						// Print the content of the file received
						AppConfig.timestampedStandardPrint("Key:" + key + ", [File Content]:\n" + value.getContent());
					}
				} catch (NumberFormatException e) {
					AppConfig.timestampedErrorPrint("Got TELL_GET message with bad text: " + clientMessage.getMessageText());
				}
			} else {
				AppConfig.timestampedErrorPrint("Got TELL_GET message with bad text: " + clientMessage.getMessageText());
			}
		} else {
			AppConfig.timestampedErrorPrint("Tell get handler got a message that is not TELL_GET");
		}
	}
}
