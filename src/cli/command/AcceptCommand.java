package cli.command;

import app.AppConfig;

public class AcceptCommand implements CLICommand{
    @Override
    public String commandName() {
        return "accept";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid accept command. Usage: accept [address:port]");
            return;
        }

        int port;

        try {
            port = Integer.parseInt(splitArgs[0]);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

        if(!AppConfig.chordState.getPendingFollowers().contains(port)) {
            AppConfig.timestampedErrorPrint("Port " + port + " is not in the list of pending followers.");
        } else {
            AppConfig.chordState.getPendingFollowers().remove(port);
            AppConfig.chordState.getFollowers().add(port);

            AppConfig.timestampedStandardPrint("Accepted follower: " + port);
        }
    }
}
