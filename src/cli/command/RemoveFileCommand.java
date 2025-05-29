package cli.command;

import app.AppConfig;

public class RemoveFileCommand implements CLICommand {
    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid upload command. Usage: upload [path]");
            return;
        }

        String filePath = splitArgs[0];
        int filePathHash = filePath.hashCode();
        int key = AppConfig.chordState.chordHash(filePathHash);

        if (key < 0 || key >= AppConfig.chordState.CHORD_SIZE) {
            AppConfig.timestampedErrorPrint("Invalid file path hash: " + filePathHash);
            return;
        }

        AppConfig.chordState.removeFile(key, filePath);
    }
}
