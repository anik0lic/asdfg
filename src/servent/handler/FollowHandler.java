package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.FollowMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

public class FollowHandler implements MessageHandler{

    private Message clientMessage;

    public FollowHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.FOLLOW) {
            AppConfig.timestampedErrorPrint("FollowHandler received a message that is not a FOLLOW message: " + clientMessage);
            return;
        }

        int chordId = Integer.parseInt(clientMessage.getMessageText());

        if (AppConfig.chordState.isKeyMine(chordId)) {
            // add to pending followers
            AppConfig.chordState.followNode(chordId);
        } else {
            // forward the message to the next node
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            MessageUtil.sendMessage(new FollowMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText()));
        }


//        if (AppConfig.myServentInfo.getChordId() == chordId) {
//            if (AppConfig.chordState.getFollowers().contains(chordId)) {
//                AppConfig.timestampedStandardPrint("Already following " + chordId);
//            } else if (AppConfig.chordState.getPendingFollowers().contains(chordId)) {
//                AppConfig.timestampedStandardPrint("Already pending to follow " + chordId);
//            } else {
//                AppConfig.chordState.getPendingFollowers().add(chordId);
//                AppConfig.timestampedStandardPrint("Added " + chordId + " to pending followers");
//            }
//        } else {
//            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
//            MessageUtil.sendMessage(new FollowMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText()));
//        }

    }
}
