package servent.message;

import app.ServentInfo;

import java.util.Map;

public class RemoveNodeMessage extends BasicMessage{

    private ServentInfo removedNodeInfo;
    private Map<Integer, String> transferredKeys;

    public RemoveNodeMessage(int senderPort, int receiverPort, ServentInfo removedNodeInfo, Map<Integer, String> transferredKeys
    ) {
        super(MessageType.REMOVE_NODE, senderPort, receiverPort);
        this.removedNodeInfo = removedNodeInfo;
        this.transferredKeys = transferredKeys;

    }

    public ServentInfo getRemovedNodeInfo() {
        return removedNodeInfo;
    }

    public Map<Integer, String> getTransferredKeys() {
        return transferredKeys;
    }


}
