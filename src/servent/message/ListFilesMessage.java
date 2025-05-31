package servent.message;

public class ListFilesMessage extends BasicMessage {

    private static final long serialVersionUID = 4574223442127636L;

    public ListFilesMessage(int senderPort, int receiverPort) {
        super(MessageType.LIST_FILES, senderPort, receiverPort);
    }
}
