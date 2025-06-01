package cli.command;

import app.AppConfig;
import servent.message.ListFilesMessage;
import servent.message.util.MessageUtil;

public class ListFilesCommand implements CLICommand {
    @Override
    public String commandName() {
        return "list_files";
    }

    @Override
    public void execute(String args) {
        int port;

        try {
            port = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

        if (port == AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedStandardPrint("Files for node: " + AppConfig.myServentInfo.getListenerPort() + " are: " + AppConfig.chordState.getValueMap());
        } else {
            AppConfig.timestampedStandardPrint("Sending message to node " + port + " to list files, from node " + AppConfig.myServentInfo.getListenerPort());
            MessageUtil.sendMessage(new ListFilesMessage(AppConfig.myServentInfo.getListenerPort(), port));
        }
    }
}
