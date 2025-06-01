package servent.message;

import java.util.Map;

public class BackupMessage extends BasicMessage {

    private String whereToAdd;
    private Map<Integer, String> files;

    public BackupMessage(int senderId, int receiverId, String command, String whereToAdd, Map<Integer, String> files) {
        super(MessageType.BACKUP, senderId, receiverId, command);

        this.whereToAdd = whereToAdd;
        this.files = files;
    }

    public String getWhereToAdd() {
        return whereToAdd;
    }

    public Map<Integer, String> getFiles() {
        return files;
    }
}
