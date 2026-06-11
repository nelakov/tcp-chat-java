package chat.server;

import chat.shared.Message;

import java.util.ArrayList;
import java.util.List;

public class ArchiveMessage {
    private static final ArchiveMessage INSTANCE = new ArchiveMessage();

    private final List<Message> messages = new ArrayList<>();

    private ArchiveMessage() {
    }

    public void add(Message message) {
        messages.add(message);
    }

    public List<Message> getAll() {
        return new ArrayList<>(messages);
    }

    public static ArchiveMessage getArchiveMessage() {
        return INSTANCE;
    }
}
