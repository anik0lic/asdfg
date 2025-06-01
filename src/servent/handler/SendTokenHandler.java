package servent.handler;

import app.AppConfig;
import app.SuzukiKasamiState;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.SendTokenMessage;

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
            AppConfig.timestampedStandardPrint("Received SEND_TOKEN message text: " + clientMessage.getMessageText());
            AppConfig.mutex.receiveToken(sendTokenMessage.getTokenNumbers(), sendTokenMessage.getTokenQueue());
        }
    }
}
