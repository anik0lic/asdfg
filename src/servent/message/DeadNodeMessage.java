package servent.message;

public class DeadNodeMessage extends BasicMessage{

    public DeadNodeMessage(int senderPort, int receiverPort, int deadNodePort) {
        super(MessageType.DEAD_NODE, senderPort, receiverPort, String.valueOf(deadNodePort));
    }

}
