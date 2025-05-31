package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class ListFilesResponseHandler implements MessageHandler {

    private Message clientMessage;

    public ListFilesResponseHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LIST_FILES_RESPONSE) {
            AppConfig.timestampedErrorPrint("LIST_FILES_RESPONSE got invalid type: " + clientMessage.getMessageType());
            return;
        }

        if (clientMessage.getMessageText() == null || clientMessage.getMessageText().isEmpty()) {
            AppConfig.timestampedStandardPrint("No files found in the directory for node." + clientMessage.getSenderPort());
        } else {
            String files = clientMessage.getMessageText();
            AppConfig.timestampedStandardPrint("Files in the directory: " + files + " from node " + clientMessage.getSenderPort());
        }
    }
}
