package chat.client;

import chat.shared.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatClientTest {
    private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void captureStdout() {
        originalOut = System.out;
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    @Test
    void printsOnlyMessagesBeyondCursor() {
        List<Message> archive = List.of(
                new Message("already shown", "other"),
                new Message("fresh", "other"));

        int newCursor = ChatClient.printNewForeignMessages(archive, 1, "me");

        assertEquals(2, newCursor);
        assertEquals("fresh", printedLines());
    }

    @Test
    void skipsOwnMessagesButCountsThemIntoCursor() {
        List<Message> archive = List.of(
                new Message("mine", "me"),
                new Message("theirs", "other"));

        int newCursor = ChatClient.printNewForeignMessages(archive, 0, "me");

        assertEquals(2, newCursor);
        assertEquals("theirs", printedLines());
    }

    @Test
    void printsNothingWhenCursorIsAtEnd() {
        List<Message> archive = List.of(new Message("old", "other"));

        int newCursor = ChatClient.printNewForeignMessages(archive, 1, "me");

        assertEquals(1, newCursor);
        assertEquals("", printedLines());
    }

    @Test
    void emptyArchiveYieldsZeroCursor() {
        assertEquals(0, ChatClient.printNewForeignMessages(List.of(), 0, "me"));
        assertEquals("", printedLines());
    }

    private String printedLines() {
        return capturedOut.toString(StandardCharsets.UTF_8).strip();
    }
}
