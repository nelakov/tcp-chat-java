package chat.shared;

public class MessageSerializer {
    public static String serializer(Message message){
        return message.getText() + ";" + message.getSessionId();
    }
}
