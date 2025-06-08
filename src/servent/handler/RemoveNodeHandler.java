package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.*;
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

        AppConfig.mutex.requestCriticalSection();
        try {
            RemoveNodeMessage removeNodeMessage = (RemoveNodeMessage) clientMessage;
            int removedNodePort = removeNodeMessage.getSenderPort();
            Map<Integer, String> transferredKeys = removeNodeMessage.getTransferredKeys();

            if (clientMessage.getSenderPort() == AppConfig.chordState.getPredecessor().getListenerPort()) {

                AppConfig.timestampedStandardPrint(clientMessage.getReceiverPort() + " is taking over responsibilities from node: " + removedNodePort);

                AppConfig.chordState.setPredecessor(new ServentInfo("localhost", removeNodeMessage.getPredecessorPort()));
                AppConfig.timestampedStandardPrint("New predecessor is: " + removeNodeMessage.getPredecessorPort());

                transferredKeys.forEach((key, value) -> {
                    AppConfig.chordState.getValueMap().put(key, value);
                    if (AppConfig.chordState.getAllNodeInfo().size() >= 2) {
                        MessageUtil.sendMessage(new BackupMessage(AppConfig.myServentInfo.getListenerPort(),
                                AppConfig.chordState.getPredecessor().getListenerPort(), "add", "succBack", AppConfig.chordState.getValueMap()));

                        MessageUtil.sendMessage(new BackupMessage(AppConfig.myServentInfo.getListenerPort(),
                                AppConfig.chordState.getSuccessorTable()[0].getListenerPort(), "add", "pred", AppConfig.chordState.getValueMap()));
                        AppConfig.timestampedStandardPrint("Added key: " + key + ", value: " + value);
                    }
                });

                MessageUtil.sendMessage(new RemovingUpdateMessage(removedNodePort, AppConfig.myServentInfo.getListenerPort(), ""));

            } else {
                AppConfig.timestampedStandardPrint("TEST Node " + removedNodePort + " is being removed, this is not succesor " + clientMessage.getReceiverPort());
                int chordId = AppConfig.chordState.chordHash(removedNodePort);
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
                AppConfig.timestampedStandardPrint("Next node is: " + nextNode.getListenerPort() + " for key: " + AppConfig.myServentInfo.getChordId());

                MessageUtil.sendMessage(new RemoveNodeMessage(removedNodePort, nextNode.getListenerPort(), transferredKeys));
            }
        } finally {
            AppConfig.mutex.releaseCriticalSection();
        }
    }
}
