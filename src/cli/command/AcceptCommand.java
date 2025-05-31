package cli.command;

import app.AppConfig;

public class AcceptCommand implements CLICommand{
    @Override
    public String commandName() {
        return "accept";
    }

    @Override
    public void execute(String args) {
//        String[] splitArgs = args.split(" ");
//        if (splitArgs.length != 2) {
//            AppConfig.timestampedErrorPrint("Invalid accept command. Usage: accept [address:port]");
//            return;
//        }

        int port;

        try {
            port = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

//        int nodeToAccept = AppConfig.chordState.chordHash(port);
        int nodeToAccept = port;
        AppConfig.timestampedStandardPrint("Accepting following node with chord id: " + nodeToAccept);

        AppConfig.chordState.acceptFollower(nodeToAccept);
    }
}
