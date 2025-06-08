package servent.handler;

import app.AppConfig;
import app.FailureDetection;
import servent.message.DeadNodeMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PingMessage;
import servent.message.util.MessageUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static app.FailureDetection.STRONG_FAILURE_THRESHOLD;

public class RequestConfirmationHandler implements MessageHandler{

    private final Message clientMessage;

    public RequestConfirmationHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (MessageType.REQUEST_CONFIRMATION != clientMessage.getMessageType()) {
            AppConfig.timestampedErrorPrint("RequestConfirmationHandler got a message that is not REQUEST_CONFIRMATION");
            return;
        }

        int susPort = Integer.parseInt(clientMessage.getMessageText());
        int senderPort = clientMessage.getSenderPort();

        AppConfig.timestampedStandardPrint("Received REQUEST_CONFIRMATION for suspicious node: " + susPort);

        PingMessage pingMessage = new PingMessage(AppConfig.myServentInfo.getListenerPort(), susPort);
        MessageUtil.sendMessage(pingMessage);
        AppConfig.timestampedStandardPrint("Sent PING to suspicious node: " + susPort);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(STRONG_FAILURE_THRESHOLD);
                Long lastSeenTime = AppConfig.failureDetection.getLastSeen(susPort);
                if (lastSeenTime == null || System.currentTimeMillis() - lastSeenTime > STRONG_FAILURE_THRESHOLD) {
                    AppConfig.timestampedStandardPrint("No PONG received from node: " + susPort + " within STRONG_FAILURE_THRESHOLD. Sending DEAD_NODE message to sender: " + senderPort + " for node: " + susPort);

                    MessageUtil.sendMessage(new DeadNodeMessage(AppConfig.myServentInfo.getListenerPort(), senderPort, susPort));
                } else {
                    AppConfig.timestampedStandardPrint("PONG received from node: " + susPort + ". Node is stable.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                AppConfig.timestampedErrorPrint("RequestConfirmationHandler interrupted while waiting for PONG.");
            }
        });
        executor.shutdown();

    }
}
