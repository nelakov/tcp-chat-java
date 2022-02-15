package chat.shared;

import java.util.List;

public class ListSerializer {

    public static String serializer(List<Message> list){
        String serialized = "";
        for (Message s : list) {
            serialized = serialized + MessageSerializer.serializer(s) + "\n";
        }
        return serialized;
    }
}

