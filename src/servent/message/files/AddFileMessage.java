package servent.message.files;

import app.backup.DistributedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AddFileMessage extends BasicMessage {

	private static final long serialVersionUID = 5163039209888734276L;
	private DistributedFile distributedFile;

	public AddFileMessage(int senderPort, int receiverPort, int key, DistributedFile value) {
		super(MessageType.ADD_FILE, senderPort, receiverPort, String.valueOf(key));
		distributedFile = value;
	}

	public DistributedFile getDistributedFile() {
		return distributedFile;
	}
}
