package servent.message;

public class FollowMessage extends BasicMessage {

    private static final long serialVersionUID = 17457234231556789L;

    public FollowMessage(int senderPort, int receiverPort, String portToFollow) {
        super(MessageType.FOLLOW, senderPort, receiverPort, portToFollow);
    }
}
