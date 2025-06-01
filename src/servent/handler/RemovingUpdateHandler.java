package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemovingUpdateMessage;
import servent.message.util.MessageUtil;

public class RemovingUpdateHandler implements MessageHandler {

    private Message clientMessage;

    public RemovingUpdateHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.REMOVING_UPDATE) {
            AppConfig.timestampedErrorPrint("REMOVING_UPDATE got invalid type: " + clientMessage.getMessageType());
            return;
        }

        AppConfig.timestampedStandardPrint("REMOVING UPDATE handler got message from " + clientMessage.getSenderPort() + " with text: " + (clientMessage.getMessageText()));

        if (clientMessage.getMessageText().isEmpty() || !clientMessage.getMessageText().equals(String.valueOf(AppConfig.myServentInfo.getListenerPort()))) {
            ServentInfo removeNodeInfo = new ServentInfo("localhost", clientMessage.getSenderPort());

            AppConfig.chordState.removeNode(removeNodeInfo);

            String newMessageText = clientMessage.getMessageText();
            if (newMessageText.equals("")) {
                newMessageText = String.valueOf(AppConfig.myServentInfo.getListenerPort());
            }

            Message nextUpdate = new RemovingUpdateMessage(clientMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(), newMessageText);
            MessageUtil.sendMessage(nextUpdate);
        } else {
            AppConfig.timestampedStandardPrint("End of removing update chain reached for port: " + clientMessage.getSenderPort());
        }
    }
}
