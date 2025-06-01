package cli.command;

import app.AppConfig;

public class AcceptCommand implements CLICommand{
    @Override
    public String commandName() {
        return "accept";
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

        AppConfig.chordState.acceptFollower(port);
    }
}
