package cli.command;

import app.AppConfig;

public class UploadCommand implements CLICommand {
    @Override
    public String commandName() {
        return "upload";
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

        AppConfig.chordState.getFiles().add(filePath);
        AppConfig.chordState.putValue(key, filePath);
        AppConfig.timestampedStandardPrint("File uploaded successfully: " + filePath + " with hash: " + filePathHash);
    }
}
