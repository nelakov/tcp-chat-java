package chat.server;

import chat.shared.Protocol;

public class CommandProcessor {
    private final ArchiveMessage archive = ArchiveMessage.getArchiveMessage();

    public String process(String request) {
        String[] parts = request.split(";", 2);
        String command = parts[0];
        if (Protocol.CMD_FETCH_ALL.equals(command)) {
            return Protocol.serializeAll(archive.getAll());
        }
        if (Protocol.CMD_MESSAGE.equals(command) && parts.length == 2) {
            archive.add(Protocol.deserialize(parts[1]));
            return Protocol.ACK_DELIVERED;
        }
        throw new IllegalArgumentException("unknown command: " + command);
    }
}
