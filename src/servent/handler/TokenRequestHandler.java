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

        synchronized (SuzukiKasamiState.lock) {
            int[] requestNumbers = AppConfig.mutex.getRequestNumbers();
            requestNumbers[tokenRequestMessage.getChordId()] = Math.max(sequenceNumber, requestNumbers[tokenRequestMessage.getChordId()]);

            if (AppConfig.mutex.hasToken() && AppConfig.mutex.getRequestQueue().isEmpty() &&
                    shouldSendToken(tokenRequestMessage.getChordId(), requestNumbers, AppConfig.mutex.getLN())) {
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getListenerPort() + " has token, sending it to " + tokenRequestMessage.getSenderPort() + " with request number: " + requestNumber + " and sequence number: " + sequenceNumber);

                AppConfig.mutex.setHasToken(false);
                AppConfig.mutex.sendToken(tokenRequestMessage.getSenderPort());
            } else if (!AppConfig.mutex.hasToken()) {
                AppConfig.timestampedStandardPrint("Sending token request for chord ID: " + tokenRequestMessage.getChordId() + " with request number: " + requestNumber + " and sequence number: " + sequenceNumber);
                MessageUtil.sendMessage(new TokenRequestMessage(tokenRequestMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(), tokenRequestMessage.getChordId(), requestNumbers[tokenRequestMessage.getChordId()], sequenceNumber));
            }
        }
    }

    private boolean shouldSendToken(int chordId, int[] requestNumbers, int[] LN) {
        return requestNumbers[chordId] > LN[chordId] ||
                (requestNumbers[chordId] == LN[chordId] && chordId < AppConfig.myServentInfo.getChordId());
    }
}
