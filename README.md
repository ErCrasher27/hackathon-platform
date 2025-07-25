# Hackathon Platform

Una piattaforma **Java Swing** per la gestione completa di eventi hackathon, sviluppata come progetto universitario per
i corsi di *Object Orientation* e *Base di Dati*.

---

## 📋 Descrizione

Il sistema permette di organizzare e gestire hackathon, eventi competitivi dove team di sviluppatori collaborano per
creare soluzioni innovative.  
La piattaforma gestisce l'intero ciclo di vita dell'evento: dalla creazione dell'hackathon alla valutazione finale dei
progetti.

---

## 🎯 Funzionalità Principali

### 👑 Per Organizzatori

- Creazione e gestione di hackathon con parametri personalizzabili
- Invito e gestione dei giudici
- Monitoraggio in tempo reale di partecipanti e team
- Visualizzazione delle classifiche finali

### 🧑‍⚖️ Per Giudici

- Accettazione/rifiuto inviti per valutare hackathon
- Pubblicazione dei problemi da risolvere durante l'evento
- Valutazione dei progressi dei team con commenti
- Assegnazione voti finali (scala 0-10)

### 👥 Per Partecipanti

- Registrazione agli hackathon disponibili
- Creazione team o unione a team esistenti tramite inviti
- Caricamento progressi del progetto
- Visualizzazione problemi e feedback dei giudici

---

## 🏗️ Architettura

Il progetto segue un'architettura **MVC** strutturata su più livelli:

```
src/main/java/it/unina/hackathon/
├── controller/                   # Business logic centralizzata
├── dao/                          # Interfacce per accesso ai dati
├── gui/                          # Interfacce grafiche Swing
├── implementazioniPostgresDAO/  # Implementazioni DAO per PostgreSQL
├── model/                        # Entità del dominio
└── utils/                        # Utilities e classi di supporto
```

### Pattern Utilizzati

- **Singleton**: per il controller principale e la connessione al database
- **DAO**: per l'astrazione dell'accesso ai dati
- **MVC**: per la separazione delle responsabilità
- **Response Pattern**: per la gestione uniforme delle risposte

---

## 💻 Requisiti Tecnici

- **Java 24**
- **PostgreSQL 42.7.5**
- **Maven** per la gestione delle dipendenze
- IDE consigliato: **IntelliJ IDEA**

---

## 🚀 Installazione

### 🔧 Setup Database

```bash
# Crea il database
psql -U postgres -c "CREATE DATABASE hackathon_platform;"
```

```bash
# Importa lo schema
psql -U postgres -d hackathon_platform -f docs/database/hackathon_db.sql
```

```bash
# (Opzionale) Carica dati di test
psql -U postgres -d hackathon_platform -f docs/database/hackathon_test_data.sql
```

### 🔐 Configurazione Connessione

Modifica le credenziali in `src/main/java/it/unina/hackathon/utils/ConnessioneDatabase.java`:

```java
private final String nome = "tuo_username";
private final String password = "tua_password";
```

## 📊 Database

Il database PostgreSQL include:

- **11 tabelle principali** per gestire utenti, hackathon, team, voti, etc.
- **10+ trigger** per garantire l'integrità dei dati e automatizzare processi
- **View materializzate** per le classifiche in tempo reale
- **Vincoli complessi** per le regole di business

📄 Il diagramma ER completo è disponibile in `docs/database/`

---

## 🔐 Sicurezza e Validazioni

- Validazione input lato client e database
- Controllo ruoli utente per ogni operazione
- Trigger database per prevenire operazioni non autorizzate
- Gestione delle eccezioni a tutti i livelli

---

## 👥 Tipi di Utente

- **Organizzatore**: può creare hackathon e invitare giudici
- **Giudice**: valuta i progetti e pubblica problemi
- **Partecipante**: si registra, forma team e carica progressi

---

## 🎮 Utilizzo

1. Avvia l'applicazione dalla classe `LoginGUI`
2. Registrati scegliendo il tipo di utente appropriato
3. Effettua il login con le credenziali create
4. Naviga nella home page specifica per il tuo ruolo

### 🔑 Credenziali di Test

Se hai caricato i dati di test:

- **Organizzatore**: `admin1 / password123`
- **Giudice**: `judge1 / password123`
- **Partecipante**: `user1 / password123`

---

## 🛠️ Sviluppo

Il progetto è stato sviluppato seguendo principi di clean code e best practices Java:

- Documentazione **JavaDoc** per le interfacce principali
- Gestione consistente degli errori con **response objects**
- Separazione chiara delle responsabilità tra layer
- UI responsive con layout manager appropriati

---

## 📝 Note

- Gli hackathon devono essere programmati **almeno 3 giorni** nel futuro
- Le registrazioni chiudono automaticamente **2 giorni prima** dell'evento
- I team diventano definitivi alla chiusura delle registrazioni
- Solo i **team definitivi** possono essere votati

---

> Progetto sviluppato per il corso di **Object Orientation**  
> Università degli Studi di Napoli Federico II
