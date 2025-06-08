package servent.message;

public class RequestConfirmation extends BasicMessage{

    public RequestConfirmation(int senderPort, int receiverPort, int susPort) {
        super(MessageType.REQUEST_CONFIRMATION, senderPort, receiverPort, String.valueOf(susPort));

    }

}
