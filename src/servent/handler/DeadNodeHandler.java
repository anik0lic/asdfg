package servent.handler;

import app.AppConfig;
import app.FailureDetection;
import servent.message.Message;
import servent.message.MessageType;

public class DeadNodeHandler implements MessageHandler {

    private final Message clientMessage;

    public DeadNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (MessageType.DEAD_NODE != clientMessage.getMessageType()) {
            AppConfig.timestampedErrorPrint("DeadNodeHandler got a message that is not DEAD_NODE");
            return;
        }

        int deadNodePort = Integer.parseInt(clientMessage.getMessageText());
        AppConfig.timestampedStandardPrint("Received DEAD_NODE message for node: " + deadNodePort);

        FailureDetection failureDetection = AppConfig.failureDetection;
        failureDetection.getDeadNodes().add(deadNodePort);
        AppConfig.timestampedStandardPrint("Node " + deadNodePort + " marked as dead.");
    }

}
