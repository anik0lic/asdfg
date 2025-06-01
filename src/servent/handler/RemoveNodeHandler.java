package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveNodeMessage;
import servent.message.RemovingUpdateMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class RemoveNodeHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVE_NODE) {
            AppConfig.timestampedErrorPrint("REMOVE_NODE handler got a message that is: " + clientMessage.getMessageType());
            return;
        }

        RemoveNodeMessage removeNodeMessage = (RemoveNodeMessage) clientMessage;
        int removedNodePort = removeNodeMessage.getSenderPort();
        Map<Integer, String> transferredKeys = removeNodeMessage.getTransferredKeys();

        if (clientMessage.getSenderPort() == AppConfig.chordState.getPredecessor().getListenerPort()) {

            AppConfig.timestampedStandardPrint(clientMessage.getReceiverPort() + " is taking over responsibilities from node: " + removedNodePort);

            transferredKeys.forEach((key, value) -> {
                AppConfig.chordState.getValueMap().put(key, value);
                AppConfig.timestampedStandardPrint("Added key: " + key + ", value: " + value);
            });

            AppConfig.chordState.setPredecessor(new ServentInfo("localhost", removeNodeMessage.getPredecessorPort()));
            AppConfig.timestampedStandardPrint("New predecessor is: " + removeNodeMessage.getPredecessorPort());

//            posaljemo update sledecem
            MessageUtil.sendMessage(new RemovingUpdateMessage(removedNodePort, AppConfig.myServentInfo.getListenerPort(), ""));

        } else {
            AppConfig.timestampedStandardPrint("TEST Node " + removedNodePort + " is being removed, this is not succesor " + clientMessage.getReceiverPort());
            int chordId = AppConfig.chordState.chordHash(removedNodePort);
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            AppConfig.timestampedStandardPrint("Next node is: " + nextNode.getListenerPort() + " for key: " + AppConfig.myServentInfo.getChordId());
//            if (nextNode.getListenerPort() == removedNodePort) {
//                nextNode = AppConfig.chordState.getNextNodeForKey(nextNode.getChordId());
//            }
            MessageUtil.sendMessage(new RemoveNodeMessage(removedNodePort, nextNode.getListenerPort(), transferredKeys));
        }

//        AppConfig.chordState.removeNode(removedNodeInfo);
    }
}
