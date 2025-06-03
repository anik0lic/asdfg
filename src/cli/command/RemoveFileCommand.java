package cli.command;

import app.AppConfig;

public class RemoveFileCommand implements CLICommand {
    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        String filePath = args;

        int filePathNumber = 0;
        int scalingFactor = 1;

        for (int i = 0; i < filePath.length(); i++) {
            char c = filePath.charAt(i);
            filePathNumber += (c * scalingFactor);
            scalingFactor = (scalingFactor * 7) % 1000;
        }
        int key = AppConfig.chordState.chordHash(filePathNumber);

        if (key < 0 || key >= AppConfig.chordState.CHORD_SIZE) {
            AppConfig.timestampedErrorPrint("Invalid file path hash: " + filePathNumber);
            return;
        }

        AppConfig.chordState.removeFile(key, filePath);
    }
}
