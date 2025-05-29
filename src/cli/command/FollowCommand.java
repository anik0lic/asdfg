package cli.command;

import app.AppConfig;
import app.ServentInfo;
import servent.message.FollowMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;

public class FollowCommand implements CLICommand{
    @Override
    public String commandName() {
        return "follow";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid follow command. Usage: follow [address:port]");
            return;
        }

//        String targetAddress = splitArgs[0];
        int port;

        try {
            port = Integer.parseInt(splitArgs[0]);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

//        AppConfig.chordState.followNode(port);
        int chordId = AppConfig.chordState.chordHash(port);

        if (chordId < 0 || chordId >= AppConfig.chordState.CHORD_SIZE) {
            AppConfig.timestampedErrorPrint("Invalid chord id for follow: " + chordId);
            return;
        }

        if (AppConfig.chordState.isKeyMine(chordId)) {
            // add to pending followers
            AppConfig.chordState.followNode(chordId);
        } else {
            // forward the message to the next node
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            MessageUtil.sendMessage(new FollowMessage(clientMessage.getSenderPort(), nextNode.getListenerPort(), clientMessage.getMessageText()));
        }
    }
}
