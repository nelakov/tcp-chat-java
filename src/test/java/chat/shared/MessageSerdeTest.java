package chat.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageSerdeTest {
    MessageDeserializer deserializer = new MessageDeserializer();

    @Test
    void roundTripPreservesTextWithSemicolons() {
        Message original = new Message("hi;there;friend", "123e4567-e89b-12d3-a456-426614174000");

        Message restored = deserializer.deserialize(MessageSerializer.serialize(original));

        assertEquals("hi;there;friend", restored.getText());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", restored.getSessionId());
    }

    @Test
    void wireFormatIsSessionIdFirst() {
        Message message = new Message("hello", "abc");

        assertEquals("abc;hello", MessageSerializer.serialize(message));
    }

    @Test
    void deserializeRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> deserializer.deserialize(null));
    }

    @Test
    void deserializeRejectsLineWithoutSeparator() {
        assertThrows(IllegalArgumentException.class, () -> deserializer.deserialize("garbage"));
    }
}
