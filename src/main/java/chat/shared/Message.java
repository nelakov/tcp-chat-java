package chat.shared;

public class Message {
    private final String text;
    private final String sessionId;

    public Message(String text, String sessionId) {
        this.text = text;
        this.sessionId = sessionId;
    }

    public String getText() {
        return text;
    }

    public String getSessionId() {
        return sessionId;
    }
}
