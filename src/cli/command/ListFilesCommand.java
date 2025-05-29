package cli.command;

import app.AppConfig;
import app.ServentInfo;
import servent.message.ListFilesMessage;
import servent.message.ListFilesResponseMessage;
import servent.message.util.MessageUtil;

import java.util.List;

public class ListFilesCommand implements CLICommand {
    @Override
    public String commandName() {
        return "list_files";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");
        if (splitArgs.length != 2) {
            AppConfig.timestampedErrorPrint("Invalid list_files command. Usage: list_files [address:port]");
            return;
        }

        int port;

        try {
            port = Integer.parseInt(splitArgs[0]);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

        int chordId = AppConfig.chordState.chordHash(port);

        if (AppConfig.chordState.isKeyMine(chordId)) {
//            AppConfig.chordState.getFiles(AppConfig.myServentInfo.getListenerPort(), true);
            boolean allowed = AppConfig.chordState.isVisibility() ||
                    AppConfig.chordState.getFollowers().contains(AppConfig.myServentInfo.getListenerPort());

            if (!allowed) {
                AppConfig.timestampedErrorPrint("Access denied to Node " + AppConfig.myServentInfo.getListenerPort());
                return;
            }

            // If the key is mine, respond with the files
            List<String> files = AppConfig.chordState.getFiles();
            String content = String.join(",", files);
//            MessageUtil.sendMessage(new ListFilesResponseMessage(clientMessage.getReceiverPort(), clientMessage.getSenderPort(), content));
        } else {
            ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
            MessageUtil.sendMessage(new ListFilesMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(chordId)));
        }
    }
}
