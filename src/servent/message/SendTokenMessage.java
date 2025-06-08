package servent.message;

import java.util.Queue;

public class SendTokenMessage extends BasicMessage {

    private int chordId;
    private Queue<Integer> requestQueue;
    private int[] LN;

    public SendTokenMessage(int senderPort, int receiverPort, int chordId, Queue<Integer> requestQueue, int[] LN) {
        super(MessageType.SEND_TOKEN, senderPort, receiverPort);

        this.chordId = chordId;
        this.requestQueue = requestQueue;
        this.LN = LN;
    }

    public int getChordId() {
        return chordId;
    }

    public Queue<Integer> getRequestQueue() {
        return requestQueue;
    }

    public int[] getLN() {
        return LN;
    }
}
