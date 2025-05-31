package cli.command;

import app.AppConfig;

public class UploadCommand implements CLICommand {
    @Override
    public String commandName() {
        return "upload";
    }

    @Override
    public void execute(String args) {
//        String[] splitArgs = args.split(" ");
//        if (splitArgs.length != 2) {
//            AppConfig.timestampedErrorPrint("Invalid upload command. Usage: upload [path]");
//            return;
//        }

//        String filePath = splitArgs[0];
        String filePath = args;
//        int filePathHash = Math.abs(filePath.);

        int filePathNumber = 0;
        int scalingFactor = 1;

        // Iterate through the string and calculate a weighted sum
        for (int i = 0; i < filePath.length(); i++) {
            char c = filePath.charAt(i);              // Get each character
            filePathNumber += (c * scalingFactor);    // Multiply by a scaling factor (based on position)
            scalingFactor = (scalingFactor * 7) % 1000; // Update scaling factor, bounded to prevent large values
        }

        int key = AppConfig.chordState.chordHash(filePathNumber);

        AppConfig.timestampedErrorPrint("File path: " + filePath + ", Hash: " + filePathNumber + ", Key: " + key);

        if (key < 0 || key >= AppConfig.chordState.CHORD_SIZE) {
            AppConfig.timestampedErrorPrint("Invalid file path hash: " + filePathNumber);
            return;
        }

//        AppConfig.chordState.getFiles().add(filePath);
        AppConfig.chordState.putValue(key, filePath);
        AppConfig.timestampedStandardPrint("File uploaded successfully: " + filePath + " with key: " + key);
    }
}
