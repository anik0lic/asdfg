package cli.command;

import app.AppConfig;

public class FollowCommand implements CLICommand{
    @Override
    public String commandName() {
        return "follow";
    }

    @Override
    public void execute(String args) {
        int port;

        try {
            port = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

        int nodeToFollow = port;

        if (nodeToFollow == AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedErrorPrint("Can't follow itself");
            return;
        }

        AppConfig.chordState.followNode(nodeToFollow);
    }
}
