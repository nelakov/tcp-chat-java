package chat.shared;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolTest {

    @Test
    void roundTripPreservesTextWithSemicolons() {
        Message original = new Message("hi;there;friend", "123e4567-e89b-12d3-a456-426614174000");

        Message restored = Protocol.deserialize(Protocol.serialize(original));

        assertEquals(original, restored);
    }

    @Test
    void wireFormatIsSessionIdFirst() {
        assertEquals("abc;hello", Protocol.serialize(new Message("hello", "abc")));
    }

    @Test
    void deserializeRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Protocol.deserialize(null));
    }

    @Test
    void deserializeRejectsLineWithoutSeparator() {
        assertThrows(IllegalArgumentException.class, () -> Protocol.deserialize("garbage"));
    }

    @Test
    void batchRoundTripPreservesOrderAndContent() {
        List<Message> original = List.of(
                new Message("Hello", "12313123"),
                new Message("Privet", "1231231231"));

        List<Message> restored = Protocol.deserializeAll(Protocol.serializeAll(original));

        assertEquals(original, restored);
    }

    @Test
    void deserializeAllReturnsEmptyListForBlankInput() {
        assertTrue(Protocol.deserializeAll("").isEmpty());
        assertTrue(Protocol.deserializeAll("\n").isEmpty());
    }
}
