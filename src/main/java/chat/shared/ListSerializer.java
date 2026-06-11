package chat.shared;

import java.util.List;
import java.util.stream.Collectors;

public class ListSerializer {

    public static String serialize(List<Message> messages) {
        return messages.stream()
                .map(MessageSerializer::serialize)
                .collect(Collectors.joining("\n"));
    }
}
