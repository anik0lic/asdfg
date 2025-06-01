package servent.message;

import app.ServentInfo;

import java.util.Map;

public class RemoveNodeMessage extends BasicMessage{

    private Map<Integer, String> transferredKeys;
    private int predecessorPort;

    public RemoveNodeMessage(int senderPort, int receiverPort, Map<Integer, String> transferredKeys) {
        super(MessageType.REMOVE_NODE, senderPort, receiverPort);
        this.transferredKeys = transferredKeys;
    }

    public RemoveNodeMessage(int senderPort, int receiverPort, Map<Integer, String> transferredKeys, int predecessorPort) {
        super(MessageType.REMOVE_NODE, senderPort, receiverPort);
        this.transferredKeys = transferredKeys;
        this.predecessorPort = predecessorPort;
    }

    public Map<Integer, String> getTransferredKeys() {
        return transferredKeys;
    }

    public int getPredecessorPort() {
        return predecessorPort;
    }

}
