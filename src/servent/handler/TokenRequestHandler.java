package servent.handler;

import app.AppConfig;
import app.SuzukiKasamiState;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.TokenRequestMessage;
import servent.message.util.MessageUtil;

public class TokenRequestHandler implements MessageHandler {

    private Message clientMessage;

    public TokenRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.TOKEN_REQUEST) {
            AppConfig.timestampedErrorPrint("TOKEN_REQUEST handler got a message that is: " + clientMessage.getMessageType());
            return;
        }

        TokenRequestMessage tokenRequestMessage = (TokenRequestMessage)clientMessage;
        int requestNumber = tokenRequestMessage.getRequestNumber();
        int sequenceNumber = tokenRequestMessage.getSequenceNumber();

        if (!AppConfig.mutex.hasToken()) {
            AppConfig.timestampedStandardPrint("Token not here (" + AppConfig.myServentInfo.getListenerPort() + "), sending token request to: " + AppConfig.chordState.getNextNodePort() + " with request number: " + requestNumber + " and sequence number: " + sequenceNumber);
            MessageUtil.sendMessage(new TokenRequestMessage(tokenRequestMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(), tokenRequestMessage.getChordId(), requestNumber, sequenceNumber));
            return;
        }

        AppConfig.mutex.lock.lock();
        try {
            int[] requestNumbers = AppConfig.mutex.getRequestNumbers();
            requestNumbers[tokenRequestMessage.getChordId()] = Math.max(sequenceNumber, requestNumbers[tokenRequestMessage.getChordId()]);

            if (AppConfig.mutex.hasToken() && !AppConfig.mutex.isInCriticalSection() &&
                    requestNumbers[tokenRequestMessage.getChordId()] == AppConfig.mutex.getLN()[tokenRequestMessage.getChordId()] + 1){
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getListenerPort() + " has token, sending it to " + tokenRequestMessage.getSenderPort() + " with request number: " + requestNumber + " and sequence number: " + sequenceNumber);

                AppConfig.mutex.sendToken(tokenRequestMessage.getSenderPort());
            } else {
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getListenerPort() + " has token, but not sending it to " + tokenRequestMessage.getSenderPort() + " with request number: " + requestNumber + " and sequence number: " + sequenceNumber);
            }
        } finally {
            AppConfig.mutex.lock.unlock();
        }
    }
}
