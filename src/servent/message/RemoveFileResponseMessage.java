package servent.message;

public class RemoveFileResponseMessage extends BasicMessage {

    public RemoveFileResponseMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.REMOVE_FILE_RESPONSE, senderPort, receiverPort, text);
    }

}
