package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.ListFilesMessage;
import servent.message.ListFilesResponseMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.List;

public class ListFilesHandler implements MessageHandler {

    private Message clientMessage;

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.LIST_FILES) {
            AppConfig.timestampedErrorPrint("LIST_FILES got invalid type: " + clientMessage.getMessageType());
            return;
        }

        int chordId = Integer.parseInt(clientMessage.getMessageText());

        if (AppConfig.chordState.isKeyMine(chordId)) {
            boolean allowed = AppConfig.chordState.isVisibility() ||
                    AppConfig.chordState.getFollowers().contains(clientMessage.getSenderPort());

            if (!allowed) {
                AppConfig.timestampedErrorPrint("Access denied to Node " + clientMessage.getSenderPort());
                return;
            }

            // If the key is mine, respond with the files
            List<String> files = AppConfig.chordState.getFiles();
            String content = String.join(",", files);
            MessageUtil.sendMessage(new ListFilesResponseMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), content));
        }
        else {
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            MessageUtil.sendMessage(new ListFilesMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(chordId)));
        }
    }

}
