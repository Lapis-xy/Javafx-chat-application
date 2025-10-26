# 🏗️ Architettura del Progetto

## Panoramica Sistema

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│  ChatGUIclient  │◄───────►│   client.java    │◄───────►│   server.java   │
│   (JavaFX UI)   │         │  (Socket Logic)  │  TCP    │  (Multi-client) │
└─────────────────┘         └──────────────────┘         └─────────────────┘
       │                             │                              │
       │ Platform.runLater()         │ Thread Ricezione             │ Thread per Client
       │                             │                              │
       └─────MessageListener─────────┘                              │
                                                                    │
                                     ┌──────────────────────────────┤
                                     │                              │
                              Client 1 Thread                Client N Thread
```

## Componenti Principali

### 1. **ChatGUIclient.java** - Interfaccia Grafica
**Responsabilità:**
- Visualizzazione messaggi in `TextArea`
- Input utente tramite `TextField`
- Rendering UI con JavaFX
- Thread-safe updates con `Platform.runLater()`

**Pattern Utilizzati:**
- **MVC**: Separa UI dalla logica di business
- **Observer**: MessageListener per ricevere notifiche

**Thread:**
- JavaFX Application Thread (UI rendering)
- Callback da thread socket del client

---

### 2. **client.java** - Logica Client Socket
**Responsabilità:**
- Connessione TCP al server
- Invio messaggi tramite `PrintWriter`
- Ricezione continua messaggi (thread dedicato)
- Notifica GUI tramite callback

**Thread:**
1. **Thread Chat Console**: Legge input da `Scanner` (modalità standalone)
2. **Thread Ricezione**: Loop infinito su `BufferedReader.readLine()`

**Stream I/O:**
```java
// Output: invio messaggi al server
PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

// Input: ricezione messaggi dal server  
BufferedReader in = new BufferedReader(
    new InputStreamReader(socket.getInputStream())
);
```

---

### 3. **server.java** - Server Multi-Client
**Responsabilità:**
- Accettare connessioni multiple (`ServerSocket.accept()`)
- Gestire HashMap di stream I/O per ogni client
- Broadcast messaggi a tutti i client connessi
- Thread dedicato per ogni client

**Strutture Dati:**
```java
ArrayList<Socket> client;                           // Lista socket connessi
Map<Socket, PrintWriter> clientWriters;             // Output per ogni client
Map<Socket, BufferedReader> clientReaders;          // Input per ogni client
```

**Thread:**
1. **Thread Accettazione**: Loop su `accept()` per nuovi client
2. **Thread Lettura** (1 per client): Legge messaggi da ogni client
3. **Thread Chat Server**: Input console per broadcast

---

## Flusso Messaggi

### Invio Messaggio Client → Server → Tutti i Client

```
1. User preme Enter in ChatGUIclient
   │
   ├──► getInputfield() chiamato
   │
   ├──► chatClient.inviaMessaggio(msg)
   │
   └──► PrintWriter.println() → Socket → Server

2. Server riceve messaggio
   │
   ├──► Thread leggiMsg() su BufferedReader.readLine()
   │
   └──► System.out.println() (console server)

3. Server invia a TUTTI (Broadcast)
   │
   ├──► inviaMsg() itera su ArrayList<Socket>
   │
   └──► Per ogni client: clientWriters.get(socket).println(msg)

4. Ogni Client riceve
   │
   ├──► Thread leggiMsg() su BufferedReader.readLine()
   │
   ├──► messageListener.onMessageReceived(msg)
   │
   └──► Platform.runLater(() -> chatArea.appendText(msg))
```

---

## Thread Safety

### Problema: Aggiornare GUI da Thread Non-JavaFX
❌ **ERRATO** (crash o comportamento indefinito):
```java
// Da thread socket
chatArea.appendText(message);  // IllegalStateException!
```

✅ **CORRETTO** (thread-safe):
```java
// Da thread socket
Platform.runLater(() -> {
    chatArea.appendText(message);  // Safe!
});
```

### Perché?
- JavaFX usa un **single-threaded model** per la UI
- Solo il **JavaFX Application Thread** può modificare la GUI
- `Platform.runLater()` accoda operazioni su quel thread

---

## Pattern Design Utilizzati

### 1. **Observer Pattern** (MessageListener)
```java
// Interface
public interface MessageListener {
    void onMessageReceived(String message);
}

// Publisher (client.java)
if (messageListener != null) {
    messageListener.onMessageReceived(msg);
}

// Subscriber (ChatGUIclient.java)
chatClient.setMessageListener(new client.MessageListener() {
    @Override
    public void onMessageReceived(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }
});
```

### 2. **Thread-Per-Client Model**
- Ogni client ha un thread dedicato per la ricezione
- Scalabile per piccole/medie quantità di client
- Alternativa: Thread Pool per migliaia di client

### 3. **HashMap Lookup Pattern**
```java
// O(1) access to client-specific streams
PrintWriter writer = clientWriters.get(socket);
BufferedReader reader = clientReaders.get(socket);
```

---

## Gestione Errori

### IOException Handling
```java
try {
    Socket socket = new Socket(ip, port);
    // ... comunicazione ...
} catch (IOException e) {
    System.out.println("Errore connessione: " + e.getMessage());
}
```

### Client Disconnection
```java
// Server: rimuove client disconnesso
try {
    writer.println(msg);
} catch (Exception e) {
    client.remove(socket);  // Cleanup
}
```

---

## Scalabilità e Limiti

### Limiti Attuali
- ⚠️ Thread-per-client non scala oltre ~1000 client
- ⚠️ Nessuna autenticazione/crittografia
- ⚠️ Messaggi non persistenti (no database)

### Possibili Miglioramenti
- ✅ NIO (Non-blocking I/O) con selettori
- ✅ Thread Pool invece di thread per client
- ✅ WebSocket per client browser
- ✅ Database per storico messaggi
- ✅ TLS/SSL per comunicazione sicura
- ✅ Autenticazione utenti

---

## Testing & Debug

### Come Testare il Sistema
1. **Test Server Solo**:
   ```bash
   java server
   # Verifica: "Server avviato: 5000"
   ```

2. **Test Client Console**:
   ```bash
   java client
   # Scrivi messaggi e verifica ricezione
   ```

3. **Test GUI Client**:
   ```bash
   java --module-path "JAVAFX_PATH" --add-modules javafx.controls ChatGUIclient
   ```

4. **Test Multi-Client**:
   - Apri 3+ finestre ChatGUIclient
   - Invia messaggio da una finestra
   - Verifica apparizione in tutte le altre

### Debug Logging
Aggiungi timestamp ai messaggi:
```java
String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
System.out.println("[" + timestamp + "] " + msg);
```

---

## Conclusione

Questo progetto dimostra:
- ✅ Comprensione architetture client-server
- ✅ Multi-threading avanzato in Java
- ✅ Design patterns (Observer, MVC)
- ✅ JavaFX per UI moderne
- ✅ Gestione stream I/O e socket

**Ideale per portfolio tecnico che mostra competenze full-stack Java!** 🚀
