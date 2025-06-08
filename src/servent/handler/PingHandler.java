package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PongMessage;
import servent.message.util.MessageUtil;

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

        int senderPort = clientMessage.getSenderPort();

        AppConfig.failureDetection.updateLastSeen(senderPort);

        AppConfig.timestampedStandardPrint("Received PING from " + senderPort);
        PongMessage pong = new PongMessage(
                AppConfig.myServentInfo.getListenerPort(),
                senderPort
        );
        MessageUtil.sendMessage(pong);
    }
}
