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

        // Iterate through the string and calculate a weighted sum
        for (int i = 0; i < filePath.length(); i++) {
            char c = filePath.charAt(i);              // Get each character
            filePathNumber += (c * scalingFactor);    // Multiply by a scaling factor (based on position)
            scalingFactor = (scalingFactor * 7) % 1000; // Update scaling factor, bounded to prevent large values
        }
        int key = AppConfig.chordState.chordHash(filePathNumber);

        if (key < 0 || key >= AppConfig.chordState.CHORD_SIZE) {
            AppConfig.timestampedErrorPrint("Invalid file path hash: " + filePathNumber);
            return;
        }

        AppConfig.chordState.removeFile(key, filePath);
    }
}
