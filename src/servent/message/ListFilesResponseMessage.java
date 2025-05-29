package servent.message;

public class ListFilesResponseMessage extends BasicMessage {

    public ListFilesResponseMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.LIST_FILES_RESPONSE, senderPort, receiverPort, text);
    }

}
