package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class PongHandler implements MessageHandler{

    private Message clientMessage;

    public PongHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.PONG) {
            System.err.println("PongHandler got wrong message type: " + clientMessage.getMessageType());
            return;
        }

        int senderPort = clientMessage.getSenderPort();

        AppConfig.timestampedStandardPrint("Node " + senderPort + " is alive, received PONG message.");

        AppConfig.failureDetection.updateLastSeen(senderPort);
    }
}
