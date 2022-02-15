package chat.server;

import chat.shared.Message;

import java.util.ArrayList;
import java.util.List;

public class ArchiveMessage {
    private final static ArchiveMessage INSTANCE = new ArchiveMessage();
    private List<Message> messages;

    private ArchiveMessage(){
        messages = new ArrayList<>();
    }

    public void add(Message message){
        messages.add(message);
    }

    public List<Message> getAll(){
        return new ArrayList<>(messages); // Делаем так чтобы все явно использовали метод add / Cоздаем копию основного листа строк
    }

    public static ArchiveMessage getArchiveMessage(){
        return INSTANCE;
    }
}
