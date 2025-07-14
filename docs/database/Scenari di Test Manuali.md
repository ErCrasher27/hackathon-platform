# Scenari di Test Manuali - Hackathon

## Setup Iniziale

1. Eseguire script di creazione database
2. Eseguire script di popolamento dati
3. Avviare l'applicazione

## SCENARIO 1: REGISTRAZIONE E LOGIN

### Test 1.1: Registrazione Nuovo Utente

**Login come:** Non loggato
**Azioni:**

1. Cliccare su "Registrati"
2. Provare a registrarsi con username corto: "ab" → **Errore previsto**
3. Provare con email non valida: "test@test" → **Errore previsto**
4. Provare con password corta: "123" → **Errore previsto**
5. Registrarsi correttamente:
    - Username: nuovo.utente
    - Email: nuovo.utente@test.it
    - Password: password123
    - Nome: Nuovo
    - Cognome: Utente
    - Ruolo: Partecipante

### Test 1.2: Login

**Azioni:**

1. Provare login con credenziali errate → **Errore previsto**
2. Login con credenziali corrette: nuovo.utente / password123
3. Verificare redirect alla home

## SCENARIO 2: FUNZIONALITÀ ORGANIZZATORE

### Test 2.1: Creazione Hackathon

**Login come:** mario.rossi (organizzatore)
**Azioni:**

1. Andare su "I miei Hackathon"
2. Cliccare "Crea Nuovo Hackathon"
3. Provare a creare con date non valide (fine prima di inizio) → **Errore previsto**
4. Provare con chiusura registrazioni dopo inizio → **Errore previsto**
5. Creare hackathon valido:
    - Titolo: Test Hackathon Demo
    - Descrizione: Hackathon di test per la demo
    - Sede: Napoli - Demo Room
    - Data inizio: domani alle 9:00
    - Data fine: dopodomani alle 18:00
    - Chiusura registrazioni: oggi alle 23:59
    - Max iscritti: 10
    - Max dimensione team: 3
6. Verificare creazione e stato REGISTRAZIONI_APERTE

### Test 2.2: Invito Giudici

**Azioni:**

1. Entrare nel dettaglio dell'hackathon appena creato
2. Cliccare su "Gestisci Giudici"
3. Invitare prof.ferrari e prof.russo
4. Verificare inviti in stato PENDING

### Test 2.3: Visualizzazione Statistiche

**Azioni:**

1. Tornare a "I miei Hackathon"
2. Verificare lista hackathon creati
3. Entrare in "AI Innovation Challenge 2024" (terminato)
4. Verificare:
    - Statistiche complete
    - Classifiche finali
    - Numero partecipanti
    - Team e voti

## SCENARIO 3: FUNZIONALITÀ GIUDICE

### Test 3.1: Accettazione Invito

**Login come:** prof.ferrari (giudice)
**Azioni:**

1. Andare su "Inviti Giudice"
2. Verificare presenza invito per "Test Hackathon Demo"
3. Accettare invito
4. Verificare comparsa in "Hackathon come Giudice"

### Test 3.2: Commenti su Progressi

**Login come:** prof.ferrari
**Azioni:**

1. Entrare in "Web3 Blockchain Hackathon" (in corso)
2. Andare su "Progressi Team"
3. Selezionare un progresso esistente
4. Aggiungere commento: "Ottimo lavoro sul frontend!"
5. Verificare visualizzazione commento

### Test 3.3: Votazione Team

**Azioni:**

1. Sempre in "Web3 Blockchain Hackathon"
2. Andare su "Vota Team"
3. Provare a votare "DeFi Developers" (non definitivo) → **Errore previsto**
4. Votare "Blockchain Builders": voto 8
5. Provare a votare di nuovo lo stesso team → **Errore previsto**
6. Verificare aggiornamento classifiche

## SCENARIO 4: FUNZIONALITÀ PARTECIPANTE - REGISTRAZIONE

### Test 4.1: Registrazione Hackathon

**Login come:** nuovo.utente (partecipante appena creato)
**Azioni:**

1. Andare su "Esplora Hackathon"
2. Vedere lista hackathon disponibili
3. Entrare in "GameDev Marathon" (quasi pieno: 18/20)
4. Cliccare "Registrati"
5. Verificare registrazione avvenuta

### Test 4.2: Tentativo Registrazione Hackathon Pieno

**Login come:** lorenzo.valle (partecipante)
**Azioni:**

1. Registrare lorenzo.valle a "GameDev Marathon"
2. Login come altro partecipante non registrato
3. Provare a registrarsi (ora 20/20) → **Errore previsto: hackathon pieno**

## SCENARIO 5: GESTIONE TEAM

### Test 5.1: Creazione Team

**Login come:** nuovo.utente (già registrato a GameDev Marathon)
**Azioni:**

1. Andare su "I miei Hackathon"
2. Entrare in "GameDev Marathon"
3. Cliccare "Crea Team"
4. Nome team: "Demo Team"
5. Verificare:
    - Creazione team
    - Utente come LEADER
    - Team non definitivo

### Test 5.2: Invito Membri

**Azioni:**

1. Nella pagina del team, cliccare "Invita Membri"
2. Provare a invitare utente non registrato all'hackathon → **Errore previsto**
3. Invitare alberto.sanna (registrato a GameDev Marathon)
4. Messaggio: "Unisciti al nostro team demo!"
5. Verificare invito creato

### Test 5.3: Accettazione Invito Team

**Login come:** alberto.sanna
**Azioni:**

1. Andare su "I miei Inviti"
2. Vedere invito da "Demo Team"
3. Accettare invito
4. Verificare:
    - Appartenenza al team
    - Ruolo MEMBRO
    - Team ora ha 2/3 membri

### Test 5.4: Tentativo Cambio Team

**Login come:** alessandro.conti (già in team "AI Wizards")
**Azioni:**

1. Provare a unirsi a un altro team dello stesso hackathon → **Errore previsto**

### Test 5.5: Completamento Team

**Login come:** nuovo.utente (leader Demo Team)
**Azioni:**

1. Invitare bianca.farina
2. Login come bianca.farina e accettare
3. Verificare team pieno (3/3)
4. Provare a invitare altro membro → **Errore previsto: team pieno**

### Test 5.6: Rendere Team Definitivo

**Login come:** nuovo.utente
**Azioni:**

1. Nel team "Demo Team", cliccare "Rendi Definitivo"
2. Confermare azione
3. Verificare:
    - Team ora definitivo
    - Non più possibile aggiungere/rimuovere membri
    - Team votabile dai giudici

## SCENARIO 6: GESTIONE PROGRESSI

### Test 6.1: Caricamento Progresso

**Login come:** nuovo.utente (membro Demo Team)
**Azioni:**

1. Nella pagina team, sezione "Progressi"
2. Cliccare "Carica Progresso"
3. Compilare:
    - Titolo: Prima Milestone
    - Descrizione: Completato il design del gioco
    - File: qualsiasi file di test
4. Verificare caricamento

### Test 6.2: Tentativo Caricamento Non Autorizzato

**Login come:** mario.rossi (organizzatore, non membro team)
**Azioni:**

1. Provare a caricare progresso per un team → **Errore previsto**

## SCENARIO 7: CASI LIMITE E CONTROLLI

### Test 7.1: Eliminazione Membro da Team Definitivo

**Login come:** alessandro.conti (in team definitivo)
**Azioni:**

1. Provare a lasciare il team → **Errore previsto: team definitivo**

### Test 7.2: Modifica Ruolo Leader

**Login come:** nuovo.utente (leader con altri membri)
**Azioni:**

1. Provare a cambiare il proprio ruolo a MEMBRO → **OK se c'è altro leader**
2. Se è l'unico leader → **Errore previsto**

### Test 7.3: Voto Team Non Definitivo

**Login come:** prof.ferrari
**Azioni:**

1. In hackathon in corso, provare a votare team non definitivo → **Errore previsto**

### Test 7.4: Commento Non Autorizzato

**Login come:** prof.costa (invitato ma non accettato)
**Azioni:**

1. Provare a commentare progresso → **Errore previsto**

## SCENARIO 8: VISUALIZZAZIONI E REPORT

### Test 8.1: Dashboard Organizzatore

**Login come:** mario.rossi
**Azioni:**

1. Verificare dashboard con:
    - Hackathon creati
    - Stati diversi
    - Statistiche

### Test 8.2: Classifiche

**Login come:** qualsiasi utente
**Azioni:**

1. Entrare in hackathon terminato
2. Verificare classifica con:
    - Posizioni
    - Media voti
    - Solo team definitivi

## SCENARIO 9: WORKFLOW COMPLETO

### Test 10.1: Ciclo Completo Hackathon

1. **Organizzatore** crea hackathon
2. **Partecipanti** si registrano
3. **Partecipanti** creano team
4. **Leader** invita membri
5. **Membri** accettano inviti
6. **Team** diventa definitivo
7. **Membri** caricano progressi
8. **Giudici** commentano
9. **Hackathon** passa in stato IN_CORSO
10. **Giudici** votano
11. **Sistema** mostra classifiche

## NOTE PER LA DEMO

### Utenti di Test Principali:

- **Organizzatore:** mario.rossi / password123
- **Giudice:** prof.ferrari / password123
- **Partecipante Leader:** alessandro.conti / password123
- **Partecipante Normale:** nuovo.utente / password123

### Stati Hackathon da Mostrare:

1. **TERMINATO** - AI Innovation Challenge (classifiche complete)
2. **IN_CORSO** - Web3 Blockchain (votazioni attive)
3. **REGISTRAZIONI_CHIUSE** - Green Tech (imminente)
4. **REGISTRAZIONI_APERTE** - HealthTech (futuro)
5. **QUASI_PIENO** - GameDev Marathon (18/20 posti)

### Errori Comuni da Dimostrare:

- Registrazione dopo deadline
- Team pieno
- Votazione team non definitivo
- Utente in più team stesso hackathon
- Solo giudici accettati possono votare
- Solo membri team caricano progressi