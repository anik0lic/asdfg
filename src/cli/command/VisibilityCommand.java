package cli.command;

import app.AppConfig;

public class VisibilityCommand implements CLICommand {
    @Override
    public String commandName() {
        return "visibility";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid visibility command. Usage: visibility [public|private]");
            return;
        }

        String visibility = splitArgs[0];
        
        if (!visibility.equals("public") && !visibility.equals("private")) {
            AppConfig.timestampedErrorPrint("Invalid visibility option. Use 'public' or 'private'.");
        } else if (visibility.equals("public")) {
            AppConfig.chordState.setVisibility(true);
            AppConfig.timestampedStandardPrint("Visibility set to public.");
        } else {
            AppConfig.chordState.setVisibility(false);
            AppConfig.timestampedStandardPrint("Visibility set to private.");
        }
    }
}
