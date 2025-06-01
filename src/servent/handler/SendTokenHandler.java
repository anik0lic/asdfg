package servent.handler;

import app.AppConfig;
import app.SuzukiKasamiState;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.SendTokenMessage;
import servent.message.util.MessageUtil;

public class SendTokenHandler implements MessageHandler {

    private Message clientMessage;

    public SendTokenHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.SEND_TOKEN) {
            AppConfig.timestampedErrorPrint("SEND_TOKEN handler got a message that is: " + clientMessage.getMessageType());
            return;
        }

        synchronized (SuzukiKasamiState.lock) {
            SendTokenMessage sendTokenMessage = (SendTokenMessage) clientMessage;

            if (sendTokenMessage.getChordId() != AppConfig.myServentInfo.getChordId()) {
                AppConfig.timestampedErrorPrint("SEND_TOKEN handler received a message for a different chord ID: " + sendTokenMessage.getChordId());
                MessageUtil.sendMessage(new SendTokenMessage(sendTokenMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(),
                        sendTokenMessage.getChordId(), sendTokenMessage.getTokenQueue(), sendTokenMessage.getLN()));
                return;
            }

            AppConfig.timestampedStandardPrint("Received SEND_TOKEN message text: " + clientMessage.getMessageText());
            AppConfig.mutex.receiveToken(sendTokenMessage.getLN(), sendTokenMessage.getTokenQueue());
        }
    }
}
