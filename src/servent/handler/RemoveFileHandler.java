package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveFileMessage;
import servent.message.util.MessageUtil;

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

//        if (AppConfig.chordState.isKeyMine(key)) {
        AppConfig.chordState.removeFile(key, filePath);
//            AppConfig.timestampedStandardPrint("File with key " + key + " removed successfully.");
//        } else {
//            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(key);
//            MessageUtil.sendMessage(new RemoveFileMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText()));
//            AppConfig.timestampedErrorPrint("Key " + key + " does not belong to this node. Forwarding request.");
//        }
    }

}
