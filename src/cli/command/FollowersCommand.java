package cli.command;

import app.AppConfig;

public class FollowersCommand implements CLICommand {
    @Override
    public String commandName() {
        return "followers";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("Followers: " + AppConfig.chordState.getFollowers());
    }
}
