package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.SendTokenMessage;

import java.util.Arrays;
import java.util.LinkedList;

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

        AppConfig.mutex.lock.lock() ;
        try {
            SendTokenMessage sendTokenMessage = (SendTokenMessage) clientMessage;

            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getListenerPort() + " received SEND_TOKEN, LN: " + Arrays.toString(sendTokenMessage.getLN()) +
                    ", requestQueue: " + sendTokenMessage.getRequestQueue());

            AppConfig.mutex.setHasToken(true);
            AppConfig.mutex.setLN(sendTokenMessage.getLN());
            AppConfig.mutex.setRequestQueue(new LinkedList<>(sendTokenMessage.getRequestQueue()));

            if (AppConfig.mutex.isWaiting()) {
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getListenerPort() + " is notifying waiting threads.");
                AppConfig.mutex.tokenCondition.signalAll();
            }
        } finally {
            AppConfig.mutex.lock.unlock();
        }
    }
}
