package cli.command;

import app.AppConfig;

public class VisibilityCommand implements CLICommand {
    @Override
    public String commandName() {
        return "visibility";
    }

    @Override
    public void execute(String args) {
        String visibility = args.trim().toLowerCase();
        
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
