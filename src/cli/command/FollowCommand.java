package cli.command;

import app.AppConfig;

public class FollowCommand implements CLICommand{
    @Override
    public String commandName() {
        return "follow";
    }

    @Override
    public void execute(String args) {
//        String[] splitArgs = args.split(" ");
//        if (splitArgs.length != 2) {
//            AppConfig.timestampedErrorPrint("Invalid follow command. Usage: follow [address:port]");
//            return;
//        }

//        String targetAddress = splitArgs[0];
        int port;

        try {
            port = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

        int nodeToFollow = port;
        AppConfig.timestampedStandardPrint("Starting code for following node with chord id: " + nodeToFollow + " and port: " + port);

        if (nodeToFollow == AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedErrorPrint("Can't follow itself");
            return;
        }

        AppConfig.chordState.followNode(nodeToFollow);
    }
}
