package chat.shared;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListDeserializerTest {
    ListDeserializer listDeserializer = new ListDeserializer();

    @Test
    void shouldDeserializeNewlineSeparatedMessages() {
        String wireLines = "12313123;Hello" + "\n" +
                           "1231231231;Privet";

        List<Message> messages = listDeserializer.deserialize(wireLines);

        assertEquals("Hello", messages.get(0).getText());
        assertEquals("12313123", messages.get(0).getSessionId());
        assertEquals("Privet", messages.get(1).getText());
        assertEquals("1231231231", messages.get(1).getSessionId());
    }

    @Test
    void shouldReturnEmptyListForBlankInput() {
        assertTrue(listDeserializer.deserialize("\n").isEmpty());
        assertTrue(listDeserializer.deserialize("").isEmpty());
    }
}
