# network-chat

A multi-client TCP chat in plain Java. No frameworks, no external runtime dependencies.
A learning project focused on sockets, wire protocols, and threads, kept deliberately small
enough to read in one sitting.

![Java](https://img.shields.io/badge/Java-25-orange) ![Build](https://img.shields.io/badge/build-Maven-blue) ![Tests](https://img.shields.io/badge/tests-JUnit%206-green)

![screencast](/src/main/resources/ScreenMyChat.gif)

## How it works

A central server stores all messages in memory. Clients are poll-based: a client pushes
each typed line to the server and asks for the full archive once per second, printing
only messages it has not shown yet and filtering out its own (by session id).

```
┌──────────────┐  MSG;<sessionId>;<text>   ┌──────────────┐
│  ChatClient  │ ─────────────────────────▶ │    Server    │
│  (terminal)  │ ◀───────────────────────── │  port 6666   │
└──────────────┘        Доставлено          └──────┬───────┘
       │                                           │
       │  GET                                      ▼
       │ ─────────────────────────────▶  ┌──────────────────┐
       │ ◀─────────────────────────────  │  ArchiveMessage  │
       │   sessionId;text per line       │  (in-memory)     │
       └─ every 1s                       └──────────────────┘
```

Each request is a single short-lived TCP connection: connect, send one line, read the
reply, disconnect. The server closing the socket marks the end of the response.

## Quick start

Requires JDK 25+ and Maven.

```bash
mvn clean package

# Terminal 1: server
java -cp target/network-chat-1.0-SNAPSHOT.jar chat.server.Server

# Terminals 2..N: one per chat participant
java -cp target/network-chat-1.0-SNAPSHOT.jar chat.client.ChatClient
```

Type a line, hit Enter, and everyone else sees it within a second.

## Wire protocol

Line-oriented, UTF-8. All format knowledge lives in one class: `chat.shared.Protocol`.

| Request | Meaning | Response |
|---|---|---|
| `MSG;<sessionId>;<text>` | Store a chat message | `Доставлено` (ack) |
| `GET` | Fetch the full archive | `sessionId;text` per line |
| anything else | Rejected | connection closed, server logs the error |

Design notes:

- **`sessionId` comes first** in a message line. It is a UUID and can never contain `;`,
  so combined with `split(";", 2)` the text keeps every semicolon the user typed.
- **Commands are out-of-band.** The command token is a dedicated field, never the user's
  text, so typing `GET`, `MSG` or `0` in chat is just a message.
- **Malformed input never kills the server.** A null or garbage line fails one
  connection with a logged `IllegalArgumentException`; the accept loop keeps serving.

## Architecture

```
src/main/java/chat/
├── server/
│   ├── Server.java            accept loop; one connection at a time, fail-soft per client
│   ├── CommandProcessor.java  dispatches MSG/GET to the archive
│   └── ArchiveMessage.java    singleton in-memory message store
├── client/
│   ├── ChatClient.java        entry point; polls and prints other sessions' messages
│   ├── UserInputMessageProcessor.java  worker thread: stdin → server
│   ├── MessageService.java    one request/response exchange over a fresh socket
│   └── MessageNotDeliveredException.java
└── shared/
    ├── Message.java           record (text, sessionId)
    └── Protocol.java          the entire wire format: tokens, ack, serialization
```

**Threading.** The server is single-threaded: one client served at a time, blocking I/O.
The client runs two threads: main polls the server, a worker reads stdin. They share no
mutable state: the session id is injected via constructors, the poll cursor is a local `int`.

**Error handling.** The server treats every connection as disposable (per-connection
try-with-resources + catch). The client survives transient poll failures and reports an
undelivered message with its text instead of a bare stack trace.

## Testing

```bash
mvn test                                  # all tests
mvn test -Dtest=ProtocolTest              # one class
mvn test -Dtest=ProtocolTest#roundTripPreservesTextWithSemicolons
```

JUnit 6. The suite pins the wire format (round-trips, field order, malformed-input
rejection) and command dispatch, the contract both sides depend on. Protocol tests are
characterization tests: any format change must consciously update them.

## Design decisions & intentional simplifications

This is a learning project; some choices trade robustness for readability on purpose:

| Decision | Why |
|---|---|
| Single-threaded blocking server | The point is sockets and protocol, not concurrency. One slow client stalls the rest, known and accepted. |
| Polling instead of push | Keeps the client trivial: no persistent connections, no server-side client registry. Costs ~1s latency and O(archive) traffic per poll. |
| In-memory singleton store | No persistence by design; history dies with the server process. Unsynchronized, safe while the server is single-threaded. |
| Hardcoded `127.0.0.1:6666` | Local demo scope; both values are named constants (`Server.PORT`, `MessageService.SERVER_*`). |

Natural next steps if this ever grows up: virtual-thread-per-connection server,
push delivery over a persistent connection, incremental fetch (`GET` since cursor),
and a `SessionId` domain type once it has validation behavior to carry.

## Project conventions

- Wire format changes happen in exactly one file (`Protocol.java`) plus its test.
- Generated artifacts (`target/`) are historical baggage in git, never stage their changes.
