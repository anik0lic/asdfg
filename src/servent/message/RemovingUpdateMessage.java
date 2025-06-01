package servent.message;

public class RemovingUpdateMessage extends BasicMessage {

    private static final long serialVersionUID = 123456789L;

    public RemovingUpdateMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.REMOVING_UPDATE, senderPort, receiverPort, text);
    }
}
