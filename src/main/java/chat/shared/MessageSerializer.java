package chat.shared;

public class MessageSerializer {

    public static String serialize(Message message) {
        // sessionId first: it is a UUID and never contains ';', so the text keeps all its semicolons
        return message.getSessionId() + ";" + message.getText();
    }
}
