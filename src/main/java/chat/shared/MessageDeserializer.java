package chat.shared;

public class MessageDeserializer {

    public Message deserialize(String wireLine) {
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
}
