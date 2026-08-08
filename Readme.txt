# 🚨 STOMP Emergency Service

A client-server **Emergency Service platform** built on a custom implementation of the **STOMP** (Simple Text Oriented Messaging Protocol). Users can subscribe to emergency channels (fire, medical, police, natural disasters, etc.), report incidents, and receive real-time updates — enabling coordinated community response during a crisis.

Built as Assignment 3 for **SPL251 - Systems Programming Lab**, Ben-Gurion University.

---

## 📐 Architecture

| Component | Language | Description |
|---|---|---|
| **Server** | Java (Maven) | Centralized STOMP server supporting two concurrency models: **Thread-Per-Client (TPC)** and **Reactor** (event-driven), selectable at runtime. |
| **Client** | C++ (Makefile) | Terminal-based STOMP client (`StompEMIClient`) for logging in, subscribing to channels, reporting emergencies, and summarizing activity. |

All communication between client and server strictly follows the [STOMP 1.2](https://stomp.github.io/stomp-specification-1.2.html) frame format:

```
<COMMAND>
<header1>:<value1>
<header2>:<value2>

<body>
^@
```

### Supported STOMP Frames

**Client → Server**
- `CONNECT` — authenticate and open a session
- `SUBSCRIBE` — join a topic/channel
- `UNSUBSCRIBE` — leave a topic/channel
- `SEND` — publish a message/report to a topic
- `DISCONNECT` — gracefully close the session

**Server → Client**
- `CONNECTED` — acknowledges a successful connection
- `MESSAGE` — delivers a published message to subscribers
- `RECEIPT` — acknowledges processing of a client frame
- `ERROR` — reports a protocol violation and closes the connection

---

## 🖥️ Server Design

The server is protocol-agnostic and built around three core abstractions:

- **`Connections<T>`** — maps active client connections to unique IDs; handles routing messages to a single client or broadcasting to all subscribers of a topic.
- **`ConnectionHandler<T>`** — represents the low-level I/O channel to a single client.
- **`StompMessagingProtocol<T>`** — implements the STOMP protocol logic itself (`start`, `process`, `shouldTerminate`), decoupled from the underlying server pattern (TPC or Reactor).

This separation means the exact same protocol implementation can run under **either** concurrency model without modification.

### Run the server

```bash
cd server
mvn compile

# Thread-Per-Client mode
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer" -Dexec.args="<port> tpc"

# Reactor mode
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.stomp.StompServer" -Dexec.args="<port> reactor"
```

---

## 💻 Client Design

The C++ client runs **two threads**: one blocking on standard input for user commands, and one listening on the socket for incoming server frames — allowing the client to receive real-time updates while the user is typing.

### Build & run

```bash
cd client
make
./bin/StompWCIClient
```

The makefile compiles the following sources into `bin/`: `ConnectionHandler.cpp`, `StompClient.cpp`, `StompProtocol.cpp`, and `event.cpp`, linking against `boost_system` and `pthread`, and produces the `StompWCIClient` executable.

### Available commands

| Command | Description |
|---|---|
| `login {host:port} {username} {password}` | Connect and authenticate with the server |
| `join {channel_name}` | Subscribe to an emergency channel |
| `exit {channel_name}` | Unsubscribe from an emergency channel |
| `report {file}` | Parse a JSON events file and send each emergency event to its channel |
| `summary {channel_name} {user} {file}` | Write a formatted summary of a user's reports on a channel to a file |
| `logout` | Gracefully disconnect from the server |

### Emergency event format (JSON)

```json
{
  "channel_name": "police",
  "events": [
    {
      "event_name": "Grand Theft Auto",
      "city": "Liberty City",
      "date_time": "1762966800",
      "description": "Pink Lampadati Felon with license plate \"STOL3N1\"...",
      "general_information": {
        "active": true,
        "forces_arrival_at_scene": false
      }
    }
  ]
}
```

Reports are sorted by `date_time`, and summaries truncate descriptions to 27 characters (followed by `...` when truncated).

---

## 📂 Project Structure

```
Assignment3/
├── server/
│   ├── pom.xml
│   ├── target/                    # Maven build output
│   └── src/
│       └── main/java/bgu/spl/net/
│           ├── api/               # Generic messaging protocol & connections interfaces
│           ├── impl/stomp/        # StompServer, TPC & Reactor implementations
│           └── srv/                # Connections, ConnectionHandler implementations
├── client/
│   ├── makefile
│   ├── src/
│   │   ├── ConnectionHandler.cpp
│   │   ├── StompClient.cpp
│   │   ├── StompProtocol.cpp
│   │   └── event.cpp
│   ├── include/
│   │   ├── ConnectionHandler.h
│   │   ├── StompProtocol.h
│   │   ├── event.h
│   │   └── json.hpp
│   └── bin/                       # StompWCIClient (built executable)
└── README.md
```

---

## 🛠️ Requirements

- **Java 11+** and **Maven** (server)
- **g++** with C++11 support, **make**, and **Boost** (`boost_system`) + **pthread** (client)
- Linux environment (tested on CS labs / Docker)

---
Team
Renad Abu Shareb
Adan Abo Salok 
---

## 📄 License

This project was developed for academic purposes as part of the SPL251 course at Ben-Gurion University.


