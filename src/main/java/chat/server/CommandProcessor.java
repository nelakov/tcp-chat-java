package chat.server;

import chat.shared.ListSerializer;
import chat.shared.MessageDeserializer;
import chat.shared.Protocol;

public class CommandProcessor {
    private final ArchiveMessage archive = ArchiveMessage.getArchiveMessage();
    private final MessageDeserializer messageDeserializer = new MessageDeserializer();

    public String process(String request) {
        String[] parts = request.split(";", 2);
        String command = parts[0];
        if (Protocol.CMD_FETCH_ALL.equals(command)) {
            return ListSerializer.serialize(archive.getAll());
        }
        if (Protocol.CMD_MESSAGE.equals(command) && parts.length == 2) {
            archive.add(messageDeserializer.deserialize(parts[1]));
            return Protocol.ACK_DELIVERED;
        }
        throw new IllegalArgumentException("unknown command: " + command);
    }
}
