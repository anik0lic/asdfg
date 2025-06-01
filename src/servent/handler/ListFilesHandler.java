package servent.handler;

import app.AppConfig;
import servent.message.ListFilesResponseMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.Map;

public class ListFilesHandler implements MessageHandler {

    private Message clientMessage;

    public ListFilesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LIST_FILES) {
            AppConfig.timestampedErrorPrint("LIST_FILES got invalid type: " + clientMessage.getMessageType());
            return;
        }

        boolean allowed = AppConfig.chordState.isVisibility() ||
                    AppConfig.chordState.getFollowers().contains(clientMessage.getSenderPort());

        if (!allowed) {
            AppConfig.timestampedErrorPrint("Access denied to Node " + clientMessage.getSenderPort());
            return;
        }

        Map<Integer, String> valueMap = AppConfig.chordState.getValueMap();
        if (valueMap == null || valueMap.isEmpty()) {
            AppConfig.timestampedStandardPrint("No files found in the directory for node " + clientMessage.getReceiverPort() + " sending empty response to " + clientMessage.getSenderPort());
            MessageUtil.sendMessage(new ListFilesResponseMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), null));
            return;
        }

        MessageUtil.sendMessage(new ListFilesResponseMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), valueMap.toString()));
    }

}
