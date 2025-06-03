package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class RemoveFileHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVE_FILE) {
            AppConfig.timestampedErrorPrint("REMOVE_FILE got invalid type: " + clientMessage.getMessageType());
            return;
        }

        int key = Integer.parseInt(clientMessage.getMessageText().split(" ")[0]);
        String filePath = clientMessage.getMessageText().split(" ")[1];

        AppConfig.chordState.removeFile(key, filePath);
    }

}
