package chat.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The complete wire protocol: command tokens, the acknowledgement, and the
 * message line format. Every byte exchanged between client and server is
 * defined here and nowhere else.
 */
public final class Protocol {
    public static final String CMD_MESSAGE = "MSG";
    public static final String CMD_FETCH_ALL = "GET";
    public static final String ACK_DELIVERED = "Доставлено";

    private Protocol() {
    }

    public static String serialize(Message message) {
        // sessionId first: it is a UUID and never contains ';', so the text keeps all its semicolons
        return message.sessionId() + ";" + message.text();
    }

    public static Message deserialize(String wireLine) {
        if (wireLine == null) {
            throw new IllegalArgumentException("message line is null");
        }
        String[] fields = wireLine.split(";", 2);
        if (fields.length != 2) {
            throw new IllegalArgumentException("malformed message line: " + wireLine);
        }
        String sessionId = fields[0];
        String text = fields[1];
        return new Message(text, sessionId);
    }

    public static String serializeAll(List<Message> messages) {
        return messages.stream()
                .map(Protocol::serialize)
                .collect(Collectors.joining("\n"));
    }

    public static List<Message> deserializeAll(String wireLines) {
        List<Message> messages = new ArrayList<>();
        for (String line : wireLines.split("\n")) {
            if (!line.isBlank()) {
                messages.add(deserialize(line));
            }
        }
        return messages;
    }
}
