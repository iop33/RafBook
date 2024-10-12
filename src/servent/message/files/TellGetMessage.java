package servent.message.files;

import app.backup.DistributedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellGetMessage extends BasicMessage {

	private static final long serialVersionUID = -6213394344524749872L;
	private DistributedFile distributedFile;

	public TellGetMessage(int senderPort, int receiverPort, int key, DistributedFile value) {
		super(MessageType.TELL_GET, senderPort, receiverPort, String.valueOf(key));
		distributedFile = value;
	}

	public DistributedFile getDistributedFile() {
		return distributedFile;
	}
}
