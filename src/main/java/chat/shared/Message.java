package chat.shared;

public class Message {
    private String text;
    private String sessionId;

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

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

