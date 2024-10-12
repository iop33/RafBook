package servent.handler.files;

import java.util.Map;

import app.AppConfig;
import app.ServentInfo;
import app.backup.DistributedFile;
import servent.handler.MessageHandler;
import servent.message.*;
import servent.message.files.AskGetMessage;
import servent.message.files.TellGetMessage;
import servent.message.friends.NoPermissionMessage;
import servent.message.util.MessageUtil;

public class AskGetHandler implements MessageHandler {

	private AskGetMessage clientMessage;

	public AskGetHandler(Message clientMessage) {
		this.clientMessage =  (AskGetMessage) clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.ASK_GET) {
			try {
				int key = Integer.parseInt(clientMessage.getMessageText());
				if (AppConfig.chordState.isKeyMine(key)) {
					Map<Integer, DistributedFile> valueMap = AppConfig.chordState.getValueMap();
					DistributedFile value = null;
					boolean isPublic = false;
					int fileOwnerPort = -1;
					if (valueMap.containsKey(key)) {
						value = valueMap.get(key);
					}
					if (value != null) {
						isPublic = value.isPublic();
						fileOwnerPort = value.getOwnerPort();
					}
					// If servent requesting file is not friend of file's owner
					if (!isPublic && !clientMessage.getMyFriends().contains(fileOwnerPort)) {
						NoPermissionMessage noPermissionMessage = new NoPermissionMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort(), key);
						MessageUtil.sendMessage(noPermissionMessage);
						return;
					}
					// Send the requested file to the sender
					TellGetMessage tgm = new TellGetMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort(), key, value);
					MessageUtil.sendMessage(tgm);
				} else {
					// Forward the request to the next node
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(key);
					AskGetMessage agm = new AskGetMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText(), clientMessage.getMyFriends());
					MessageUtil.sendMessage(agm);
				}
			} catch (NumberFormatException e) {
				AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
			}
		} else {
			AppConfig.timestampedErrorPrint("Ask get handler got a message that is not ASK_GET");
		}
	}
}
