package chat.server;

import chat.shared.Protocol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandProcessorTest {
    CommandProcessor processor = new CommandProcessor();

    @Test
    void userMessageZeroIsStoredNotInterpretedAsCommand() {
        String response = processor.process(Protocol.CMD_MESSAGE + ";session-zero;0");

        assertEquals(Protocol.ACK_DELIVERED, response);
        assertTrue(processor.process(Protocol.CMD_FETCH_ALL).contains("session-zero;0"));
    }

    @Test
    void fetchAllReturnsStoredMessages() {
        processor.process(Protocol.CMD_MESSAGE + ";session-fetch;hello there");

        String archive = processor.process(Protocol.CMD_FETCH_ALL);

        assertTrue(archive.contains("session-fetch;hello there"));
    }

    @Test
    void malformedRequestThrowsInsteadOfCrashingWithRawError() {
        assertThrows(IllegalArgumentException.class, () -> processor.process("garbage-no-command"));
    }
}
