package chat.client;

import chat.shared.ListDeserializer;
import chat.shared.Message;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListDeserializerTest {
    ListDeserializer listDeserializer = new ListDeserializer();


    @Test
    void shouldDesirialized() {
        String testString = "Hello;12313123" + "\n" +
                             "Privet;1231231231";

        List<Message> messageList = listDeserializer.deserialize(testString);

        assertEquals("Hello", messageList.get(0).getText());
        assertEquals("12313123", messageList.get(0).getSessionId());
        assertEquals("Privet", messageList.get(1).getText());
        assertEquals("1231231231", messageList.get(1).getSessionId());
    }
}