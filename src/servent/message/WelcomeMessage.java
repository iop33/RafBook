package servent.message;

import app.backup.DistributedFile;

import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<Integer, DistributedFile> values;
	
	public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, DistributedFile> values) {
		super(MessageType.WELCOME, senderPort, receiverPort);
		
		this.values = values;
	}
	
	public Map<Integer, DistributedFile> getValues() {
		return values;
	}
}
