package servent.message;

public class TokenRequestMessage extends BasicMessage {

    private int chordId;
    private int requestNumber;
    private int sequenceNumber;

    public TokenRequestMessage(int senderPort, int receiverPort, int chordId, int requestNumber, int sequenceNumber) {
        super(MessageType.TOKEN_REQUEST, senderPort, receiverPort);
        this.chordId = chordId;
        this.requestNumber = requestNumber;
        this.sequenceNumber = sequenceNumber;
    }

    public int getChordId() {
        return chordId;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
