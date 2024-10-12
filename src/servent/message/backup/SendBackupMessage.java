package servent.message.backup;

import app.backup.DistributedFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class SendBackupMessage extends BasicMessage {
    private Map<Integer, DistributedFile> backup;
    public SendBackupMessage(int senderPort, int receiverPort, Map<Integer, DistributedFile> backup) {
        super(MessageType.SEND_BACKUP, senderPort, receiverPort);
        this.backup = new HashMap<>(backup);
    }

    public Map<Integer, DistributedFile> getBackup() {
        return backup;
    }
}
