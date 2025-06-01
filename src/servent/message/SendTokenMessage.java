package servent.message;

import java.util.Queue;

public class SendTokenMessage extends BasicMessage {

    private int chordId;
    private Queue<Integer> tokenQueue;
    private int[] tokenNumbers;

    public SendTokenMessage(int senderPort, int receiverPort, int chordId, Queue<Integer> tokenQueue, int[] tokenNumbers) {
        super(MessageType.SEND_TOKEN, senderPort, receiverPort);

        this.chordId = chordId;
        this.tokenQueue = tokenQueue;
        this.tokenNumbers = tokenNumbers;
    }

    public int getChordId() {
        return chordId;
    }

    public Queue<Integer> getTokenQueue() {
        return tokenQueue;
    }

    public int[] getTokenNumbers() {
        return tokenNumbers;
    }
}
