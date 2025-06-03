package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class PingHandler implements MessageHandler {

    private Message clientMessage;

    public PingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.PING) {
            AppConfig.timestampedErrorPrint("PING got invalid type: " + clientMessage.getMessageType());
            return;
        }


    }
}
