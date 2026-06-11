package chat.shared;

import java.util.ArrayList;
import java.util.List;

public class ListDeserializer {
    private final MessageDeserializer messageDeserializer = new MessageDeserializer();

    public List<Message> deserialize(String wireLines) {
        List<Message> messages = new ArrayList<>();
        for (String line : wireLines.split("\n")) {
            if (!line.isBlank()) {
                messages.add(messageDeserializer.deserialize(line));
            }
        }
        return messages;
    }
}
