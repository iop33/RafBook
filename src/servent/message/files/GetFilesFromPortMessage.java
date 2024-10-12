package servent.message.files;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class GetFilesFromPortMessage extends BasicMessage {

    public GetFilesFromPortMessage(int senderPort, int receiverPort) {
        super(MessageType.GET_PORT_FILES, senderPort, receiverPort);
    }
}
