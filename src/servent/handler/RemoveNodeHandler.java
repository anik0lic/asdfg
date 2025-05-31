package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveNodeMessage;

import java.util.Map;

public class RemoveNodeHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVE_NODE) {
            System.err.println("REMOVE_NODE handler got a message that is: " + clientMessage.getMessageType());
            return;
        }

        RemoveNodeMessage removeNodeMessage = (RemoveNodeMessage) clientMessage;
        ServentInfo removedNodeInfo = removeNodeMessage.getRemovedNodeInfo();
        Map<Integer, String> transferredKeys = removeNodeMessage.getTransferredKeys();

        // Add transferred keys to the current node's valueMap
        AppConfig.timestampedStandardPrint("Taking over responsibilities from node: " + removedNodeInfo);

        transferredKeys.forEach((key, value) -> {
            AppConfig.chordState.getValueMap().put(key, value);
            AppConfig.timestampedStandardPrint("Added key: " + key + ", value: " + value);
        });

        // Remove the node from local state
        AppConfig.chordState.removeNode(removedNodeInfo);
    }
}
