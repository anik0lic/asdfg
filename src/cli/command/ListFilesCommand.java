package cli.command;

import app.AppConfig;
import app.ServentInfo;
import servent.message.ListFilesMessage;
import servent.message.ListFilesResponseMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListFilesCommand implements CLICommand {
    @Override
    public String commandName() {
        return "list_files";
    }

    @Override
    public void execute(String args) {
//        String[] splitArgs = args.split(" ");
//        if (splitArgs.length != 2) {
//            AppConfig.timestampedErrorPrint("Invalid list_files command. Usage: list_files [address:port]");
//            return;
//        }

        int port;

        try {
//            port = Integer.parseInt(splitArgs[0]);
            port = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid port number in follow command.");
            return;
        }

        if (port == AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.timestampedErrorPrint("Files for this node: " + AppConfig.myServentInfo.getListenerPort() + " are: " + AppConfig.chordState.getValueMap());
        } else {
            AppConfig.timestampedStandardPrint("Sending message to node " + port + " to list files, from node " + AppConfig.myServentInfo.getListenerPort());
            MessageUtil.sendMessage(new ListFilesMessage(AppConfig.myServentInfo.getListenerPort(), port));
        }

        AppConfig.timestampedStandardPrint("ValueMap: " + AppConfig.chordState.getValueMap());
    }
}
