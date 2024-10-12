package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;

public class TellPortFilesMessage extends BasicMessage {

    private List<Integer> fileIds;
    public TellPortFilesMessage(int senderPort, int receiverPort, List<Integer> fileIds) {
        super(MessageType.TELL_PORT_FILES, senderPort, receiverPort);
        this.fileIds = fileIds;
    }

    public List<Integer> getFileIds() {
        return fileIds;
    }
}
