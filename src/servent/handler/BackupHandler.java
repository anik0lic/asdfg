package servent.handler;

import app.AppConfig;
import servent.message.BackupMessage;
import servent.message.Message;
import servent.message.MessageType;

public class BackupHandler implements MessageHandler {

    private Message clientMessage;

    public BackupHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.BACKUP) {
            AppConfig.timestampedStandardPrint("BACKUP handler got a message that is: " + clientMessage.getMessageType());
        }

        BackupMessage backupMessage = (BackupMessage) clientMessage;

        if (backupMessage.getWhereToAdd().equals("pred")) {
            if (backupMessage.getMessageText().equals("add")) {
                AppConfig.timestampedStandardPrint("Adding files to predecessor map: " + backupMessage.getFiles());

                AppConfig.chordState.clearPredecessorValueMap();
                AppConfig.chordState.setPredecessorValueMap(backupMessage.getFiles());
            } else if (backupMessage.getMessageText().equals("remove")) {
                AppConfig.timestampedStandardPrint("Removing files from predecessor map: " + backupMessage.getFiles());

                AppConfig.chordState.clearPredecessorValueMap();
                AppConfig.chordState.setPredecessorValueMap(backupMessage.getFiles());
            }
        } else if (backupMessage.getWhereToAdd().equals("succ")) {
            if (backupMessage.getMessageText().equals("add")) {
                AppConfig.timestampedStandardPrint("Adding files to successor map: " + backupMessage.getFiles());

                AppConfig.chordState.clearSuccessorValueMap();
                AppConfig.chordState.setSuccessorValueMap(backupMessage.getFiles());
            } else if (backupMessage.getMessageText().equals("remove")) {
                AppConfig.timestampedStandardPrint("Removing files from successor map: " + backupMessage.getFiles());

                AppConfig.chordState.clearSuccessorValueMap();
                AppConfig.chordState.setSuccessorValueMap(backupMessage.getFiles());
            }
        } else {
            AppConfig.timestampedErrorPrint("Unknown destination for backup: " + backupMessage.getWhereToAdd());
        }
    }
}
