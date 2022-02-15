package chat.shared;

import java.util.ArrayList;
import java.util.List;

public class ListDeserializer {
    public List<Message> deserialize(String list) {
        MessageDeserializer deserializer = new MessageDeserializer();
        List<Message> listMessage = new ArrayList<>();
        String[] arrMessage = list.split("\n");

        for (String s : arrMessage) {
          listMessage.add(deserializer.deserialize(s));
        }
        return listMessage;
    }
}
