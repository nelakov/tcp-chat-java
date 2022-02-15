package chat.shared;

public class MessageDeserializer {
    public Message deserialize(String message) {
        String[] words = message.split(";");
        String text = words[0];
        String sessionId = words[1];
        return new Message(text, sessionId);
    }
}

