package cli.command;

import app.AppConfig;

public class PendingCommand implements CLICommand {
    @Override
    public String commandName() {
        return "pending";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("Pending followers: " + AppConfig.chordState.getPendingFollowers());
//        AppConfig.chordState.getPendingFollowers(AppConfig.myServentInfo.getChordId());
    }
}
