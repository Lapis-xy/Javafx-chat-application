# Chat Application - JavaFX

Una moderna applicazione di chat client-server sviluppata in Java con interfaccia grafica JavaFX.

## 🎯 Caratteristiche

- ✅ **Interfaccia grafica moderna** con JavaFX
- ✅ **Architettura client-server** con socket TCP
- ✅ **Comunicazione in tempo reale** tra utenti
- ✅ **Design responsivo** con tema scuro
- ✅ **Multi-threading** per gestione connessioni simultanee

## 🛠️ Tecnologie Utilizzate

- **Java 23**
- **JavaFX 25** per l'interfaccia grafica
- **Socket Programming** per la comunicazione di rete
- **Multi-threading** per gestione connessioni concorrenti

## 📋 Requisiti

- JDK 23 o superiore
- JavaFX SDK 25
- VS Code con Extension Pack for Java (opzionale)

## 🚀 Come Eseguire

### Metodo 1: Con VS Code

1. Clona il repository
2. Apri la cartella in VS Code
3. Premi `F5` per avviare il client o il server

### Metodo 2: Da Terminale

**Avvia il Server:**
```bash
javac server.java
java server
```

**Avvia il Client:**
```bash
javac --module-path "PATH_TO_JAVAFX/lib" --add-modules javafx.controls ChatGUIclient.java client.java
java --module-path "PATH_TO_JAVAFX/lib" --add-modules javafx.controls ChatGUIclient
```

## 📸 Screenshot

### Interfaccia Client
![Chat GUI](screenshots/chat-gui.png)
*Interfaccia grafica moderna con tema scuro e bordi arrotondati*

### Server Multi-Client
![Multiple Clients](screenshots/multiple-clients.png)
*Server che gestisce connessioni simultanee con broadcast messaggi*

### Design & Styling
![Dark Theme](screenshots/dark-theme.png)
*TextField personalizzato con effetti di trasparenza e bordi stilizzati*

> **Nota**: Per catturare gli screenshot, segui le istruzioni in `screenshots/README.md`

## 🏗️ Architettura

### Server
- Gestisce connessioni multiple tramite thread dedicati
- Broadcast dei messaggi a tutti i client connessi
- HashMap per mappare socket a stream di I/O

### Client
- Interfaccia grafica con JavaFX
- Thread separato per ricezione messaggi
- Pattern Listener per aggiornamenti UI in tempo reale

**📖 [Leggi la documentazione completa dell'architettura →](ARCHITECTURE.md)**

## 📚 Struttura del Codice

```
Chat-JavaFX/
├── 📄 README.md              # Documentazione principale
├── 📄 LICENSE                # Licenza MIT
├── 📄 ARCHITECTURE.md        # Documentazione architettura dettagliata
├── 📄 .gitignore             # File da escludere da Git
├── 📄 .gitattributes         # Configurazione Git
├── 📁 .vscode/               # Configurazioni VS Code
│   ├── settings.json         # Reference JavaFX SDK
│   └── launch.json           # Launch configs con vmArgs
├── 📄 ChatGUIclient.java     # Interfaccia grafica JavaFX
├── 📄 client.java            # Logica client socket
├── 📄 server.java            # Server multi-client
└── 📁 screenshots/           # Screenshot applicazione
    └── *.png                 # Immagini (da aggiungere)
```

## 🔮 Sviluppi Futuri

- [ ] Autenticazione utenti
- [ ] Chat private 1-to-1
- [ ] Salvataggio cronologia messaggi
- [ ] Emoji e file sharing
- [ ] Crittografia end-to-end

## 👨‍💻 Autore

- GitHub: [@Lapis-xy](https://github.com/Lapis-xy)
- Portfolio: [Lapis-xy.github.io](https://Lapis-xy.github.io)

---

## 🎓 Competenze Dimostrate in Questo Progetto

### Programmazione Java
- ✅ Socket Programming e comunicazione di rete
- ✅ Multi-threading e gestione concorrenza
- ✅ Design Patterns (Observer/Listener)
- ✅ Exception handling e gestione errori
- ✅ Collection Framework (ArrayList, HashMap)

### JavaFX & UI/UX
- ✅ Applicazioni JavaFX con Scene Builder
- ✅ Styling CSS in JavaFX
- ✅ Layout responsivi (StackPane, VBox)
- ✅ Event handling e user interaction
- ✅ Thread-safe GUI updates (Platform.runLater)

### Architettura Software
- ✅ Separazione logica/presentazione (MVC pattern)
- ✅ Client-Server architecture
- ✅ Thread-per-client model per scalabilità
- ✅ Stream I/O con BufferedReader/PrintWriter
- ✅ Callback pattern per comunicazione asincrona

## 📄 Licenza

MIT License - vedi file [LICENSE](LICENSE) per dettagli

---

## 📚 Documentazione Aggiuntiva

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Architettura dettagliata del sistema con diagrammi e spiegazione design patterns

---

## ⭐ Supporto

Se questo progetto ti è stato utile, lascia una stella! ⭐

Per domande o suggerimenti, apri una [Issue](../../issues) su GitHub.

