-- ==================================================
-- TABELLE ENUM (per maggiore flessibilità)
-- ==================================================

CREATE TABLE user_roles
(
    role_id     SERIAL PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    descrizione TEXT
);

CREATE TABLE hackathon_status
(
    status_id   SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE invito_status
(
    status_id   SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO user_roles (role_name, descrizione)
VALUES ('PARTECIPANTE', 'Utente che partecipa agli hackathon come membro di un team'),
       ('ORGANIZZATORE', 'Utente che può creare e gestire hackathon'),
       ('GIUDICE', 'Utente che può valutare i team negli hackathon');

INSERT INTO hackathon_status (status_name)
VALUES ('REGISTRAZIONI_APERTE'),
       ('REGISTRAZIONI_CHIUSE'),
       ('IN_CORSO'),
       ('TERMINATO');

INSERT INTO invito_status (status_name)
VALUES ('PENDING'),
       ('ACCEPTED'),
       ('DECLINED');

-- ==================================================
-- TABELLE PRINCIPALI
-- ==================================================

CREATE TABLE utenti
(
    utente_id          SERIAL PRIMARY KEY,
    username           VARCHAR(50)  NOT NULL UNIQUE,
    email              VARCHAR(100) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL,
    nome               VARCHAR(50)  NOT NULL,
    cognome            VARCHAR(50)  NOT NULL,
    tipo_utente_id     INTEGER      NOT NULL REFERENCES user_roles (role_id),
    data_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Vincolo email format
    CONSTRAINT check_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
) );

CREATE TABLE hackathon
(
    hackathon_id                SERIAL PRIMARY KEY,
    titolo                      VARCHAR(200) NOT NULL,
    descrizione                 TEXT,
    sede                        VARCHAR(200) NOT NULL,
    data_inizio                 TIMESTAMP    NOT NULL,
    data_fine                   TIMESTAMP    NOT NULL,
    data_chiusura_registrazioni TIMESTAMP    NOT NULL,
    max_iscritti                INTEGER      NOT NULL CHECK (max_iscritti > 0),
    max_dimensione_team         INTEGER      NOT NULL CHECK (max_dimensione_team > 0),
    organizzatore_id            INTEGER      NOT NULL REFERENCES utenti (utente_id),
    status_id                   INTEGER      NOT NULL REFERENCES hackathon_status (status_id),
    data_creazione              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Vincoli temporali
    CONSTRAINT check_date_order CHECK (data_inizio < data_fine),
    CONSTRAINT check_registration_deadline CHECK (data_chiusura_registrazioni < data_inizio)
);

CREATE TABLE team
(
    team_id        SERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    hackathon_id   INTEGER      NOT NULL REFERENCES hackathon (hackathon_id),
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    definitivo     BOOLEAN   DEFAULT FALSE,

    -- Ogni team ha nome univoco per hackathon
    UNIQUE (nome, hackathon_id)
);

CREATE TABLE problema
(
    problema_id        SERIAL PRIMARY KEY,
    hackathon_id       INTEGER      NOT NULL REFERENCES hackathon (hackathon_id),
    titolo             VARCHAR(200) NOT NULL,
    descrizione        TEXT         NOT NULL,
    data_pubblicazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pubblicato_da      INTEGER      NOT NULL REFERENCES utenti (utente_id)
);

-- ==================================================
-- TABELLE DI RELAZIONE (derivanti da N:N)
-- ==================================================

-- Registrazioni utenti agli hackathon
CREATE TABLE registrazioni
(
    registrazione_id   SERIAL PRIMARY KEY,
    utente_id          INTEGER NOT NULL REFERENCES utenti (utente_id),
    hackathon_id       INTEGER NOT NULL REFERENCES hackathon (hackathon_id),
    data_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    team_id            INTEGER REFERENCES team (team_id),

    -- Un utente può registrarsi una sola volta per hackathon
    UNIQUE (utente_id, hackathon_id)
);

-- Giudici assegnati agli hackathon
CREATE TABLE giudici_hackathon
(
    giudice_hackathon_id SERIAL PRIMARY KEY,
    hackathon_id         INTEGER NOT NULL REFERENCES hackathon (hackathon_id),
    giudice_id           INTEGER NOT NULL REFERENCES utenti (utente_id),
    data_invito          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    invitato_da          INTEGER NOT NULL REFERENCES utenti (utente_id),
    stato_invito_id      INTEGER   DEFAULT 1 REFERENCES invito_status (status_id), -- 1 = PENDING

    -- Un giudice può essere invitato una sola volta per hackathon
    UNIQUE (hackathon_id, giudice_id)
);

-- Membri dei team
CREATE TABLE membri_team
(
    membro_team_id SERIAL PRIMARY KEY,
    team_id        INTEGER NOT NULL REFERENCES team (team_id),
    utente_id      INTEGER NOT NULL REFERENCES utenti (utente_id),
    data_ingresso  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    ruolo_team     VARCHAR(20) DEFAULT 'MEMBRO' CHECK (ruolo_team IN ('LEADER', 'MEMBRO')),

    -- Un utente può essere in un solo team per hackathon (gestito via trigger)
    UNIQUE (team_id, utente_id)
);

-- Progressi caricati dai team
CREATE TABLE progressi
(
    progresso_id     SERIAL PRIMARY KEY,
    team_id          INTEGER      NOT NULL REFERENCES team (team_id),
    titolo           VARCHAR(200) NOT NULL,
    descrizione      TEXT,
    documento_path   VARCHAR(500),
    documento_nome   VARCHAR(200),
    data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    caricato_da      INTEGER      NOT NULL REFERENCES utenti (utente_id)
);

-- Commenti dei giudici sui progressi
CREATE TABLE commenti
(
    commento_id   SERIAL PRIMARY KEY,
    progresso_id  INTEGER NOT NULL REFERENCES progressi (progresso_id),
    giudice_id    INTEGER NOT NULL REFERENCES utenti (utente_id),
    testo         TEXT    NOT NULL,
    data_commento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Voti dei giudici ai team
CREATE TABLE voti
(
    voto_id             SERIAL PRIMARY KEY,
    hackathon_id        INTEGER NOT NULL REFERENCES hackathon (hackathon_id),
    team_id             INTEGER NOT NULL REFERENCES team (team_id),
    giudice_id          INTEGER NOT NULL REFERENCES utenti (utente_id),
    valore              INTEGER NOT NULL CHECK (valore >= 0 AND valore <= 10),
    criteri_valutazione TEXT,
    data_voto           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Un giudice può votare un team una sola volta per hackathon
    UNIQUE (hackathon_id, team_id, giudice_id)
);

-- Inviti per unirsi ai team
CREATE TABLE inviti_team
(
    invito_id               SERIAL PRIMARY KEY,
    team_id                 INTEGER NOT NULL REFERENCES team (team_id),
    invitante_id            INTEGER NOT NULL REFERENCES utenti (utente_id),
    invitato_id             INTEGER NOT NULL REFERENCES utenti (utente_id),
    messaggio_motivazionale TEXT,
    stato_invito_id         INTEGER   DEFAULT 1 REFERENCES invito_status (status_id), -- 1 = PENDING
    data_invito             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_risposta           TIMESTAMP,

    -- Un invito non può essere duplicato
    UNIQUE (team_id, invitato_id)
);

-- ==================================================
-- INDICI PER PERFORMANCE (query frequenti)
-- ==================================================

CREATE INDEX idx_utenti_tipo ON utenti (tipo_utente_id);
CREATE INDEX idx_hackathon_organizzatore ON hackathon (organizzatore_id);
CREATE INDEX idx_hackathon_status ON hackathon (status_id);
CREATE INDEX idx_hackathon_date_range ON hackathon (data_inizio, data_fine);
CREATE INDEX idx_registrazioni_hackathon ON registrazioni (hackathon_id);
CREATE INDEX idx_registrazioni_utente ON registrazioni (utente_id);
CREATE INDEX idx_team_hackathon ON team (hackathon_id);
CREATE INDEX idx_membri_team_utente ON membri_team (utente_id);
CREATE INDEX idx_voti_hackathon_team ON voti (hackathon_id, team_id);
CREATE INDEX idx_progressi_team ON progressi (team_id);
CREATE INDEX idx_giudici_hackathon ON giudici_hackathon (giudice_id);
CREATE INDEX idx_giudici_stato ON giudici_hackathon (stato_invito_id);
CREATE INDEX idx_inviti_team_stato ON inviti_team (stato_invito_id);

-- ==================================================
-- TRIGGER E FUNZIONI DI CONTROLLO
-- ==================================================

-- Funzione per verificare che solo organizzatori possano creare hackathon
CREATE
OR REPLACE FUNCTION check_organizzatore_role()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti u 
        JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
        WHERE u.utente_id = NEW.organizzatore_id 
        AND ur.role_name = 'ORGANIZZATORE'
    ) THEN
        RAISE EXCEPTION 'Solo gli organizzatori possono creare hackathon';
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Trigger per il controllo ruolo organizzatore
CREATE TRIGGER trigger_check_organizzatore_role
    BEFORE INSERT OR
UPDATE ON hackathon FOR EACH ROW EXECUTE FUNCTION check_organizzatore_role();

-- Funzione per verificare che solo giudici possano essere invitati come giudici
CREATE
OR REPLACE FUNCTION check_giudice_role()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti u 
        JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
        WHERE u.utente_id = NEW.giudice_id 
        AND ur.role_name = 'GIUDICE'
    ) THEN
        RAISE EXCEPTION 'Solo gli utenti con ruolo GIUDICE possono essere invitati come giudici';
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Trigger per il controllo ruolo giudice
CREATE TRIGGER trigger_check_giudice_role
    BEFORE INSERT OR
UPDATE ON giudici_hackathon FOR EACH ROW EXECUTE FUNCTION check_giudice_role();

-- Funzione per verificare che un utente sia in un solo team per hackathon
CREATE
OR REPLACE FUNCTION check_single_team_per_hackathon()
RETURNS TRIGGER AS $$
BEGIN
    -- Verifica se l'utente è già in un altro team per lo stesso hackathon
    IF
EXISTS (
        SELECT 1 
        FROM membri_team mt
        JOIN team t ON mt.team_id = t.team_id
        JOIN team new_team ON new_team.team_id = NEW.team_id
        WHERE mt.utente_id = NEW.utente_id 
        AND t.hackathon_id = new_team.hackathon_id
        AND mt.team_id != NEW.team_id
    ) THEN
        RAISE EXCEPTION 'Un utente può essere membro di un solo team per hackathon';
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Trigger per il controllo team unico
CREATE TRIGGER trigger_single_team_per_hackathon
    BEFORE INSERT OR
UPDATE ON membri_team FOR EACH ROW EXECUTE FUNCTION check_single_team_per_hackathon();

-- Funzione per verificare limiti team
CREATE
OR REPLACE FUNCTION check_team_size_limit()
RETURNS TRIGGER AS $$
DECLARE
max_size INTEGER;
    current_size
INTEGER;
BEGIN
    -- Ottieni il limite massimo per questo hackathon
SELECT h.max_dimensione_team
INTO max_size
FROM hackathon h
         JOIN team t ON t.hackathon_id = h.hackathon_id
WHERE t.team_id = NEW.team_id;

-- Conta i membri attuali del team
SELECT COUNT(*)
INTO current_size
FROM membri_team
WHERE team_id = NEW.team_id;

-- Verifica il limite
IF
current_size >= max_size THEN
        RAISE EXCEPTION 'Il team ha raggiunto la dimensione massima di % membri', max_size;
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Trigger per il controllo dimensione team
CREATE TRIGGER trigger_team_size_limit
    BEFORE INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION check_team_size_limit();

-- ==================================================
-- VIEW UTILI PER REPORTING
-- ==================================================

-- Vista classifiche hackathon
CREATE VIEW classifiche_hackathon AS
SELECT h.hackathon_id,
       h.titolo                         AS hackathon_titolo,
       t.team_id,
       t.nome                           AS team_nome,
       ROUND(AVG(v.valore::numeric), 2) AS media_voti,
       COUNT(v.voto_id)                 AS numero_voti,
       RANK()                              OVER (PARTITION BY h.hackathon_id ORDER BY AVG(v.valore::numeric) DESC) AS posizione
FROM hackathon h
         JOIN team t ON t.hackathon_id = h.hackathon_id
         LEFT JOIN voti v ON v.team_id = t.team_id AND v.hackathon_id = h.hackathon_id
GROUP BY h.hackathon_id, h.titolo, t.team_id, t.nome
ORDER BY h.hackathon_id, posizione;

-- Vista dettagli team con ruoli membri
CREATE VIEW dettagli_team AS
SELECT t.team_id,
       t.nome                                                       AS team_nome,
       h.titolo                                                     AS hackathon_titolo,
       COUNT(mt.utente_id)                                          AS numero_membri,
       h.max_dimensione_team,
       STRING_AGG(u.username || ' (' || mt.ruolo_team || ')', ', ') AS membri_dettagli,
       t.definitivo
FROM team t
         JOIN hackathon h ON t.hackathon_id = h.hackathon_id
         LEFT JOIN membri_team mt ON mt.team_id = t.team_id
         LEFT JOIN utenti u ON u.utente_id = mt.utente_id
GROUP BY t.team_id, t.nome, h.titolo, h.max_dimensione_team, t.definitivo;

-- Vista statistiche hackathon
CREATE
OR REPLACE VIEW statistiche_hackathon AS
SELECT h.hackathon_id,
       h.titolo,
       h.sede,
       h.data_inizio,
       h.data_fine,
       hs.status_name                AS stato,
       u_org.username                AS organizzatore,
       COUNT(DISTINCT r.utente_id)   AS totale_iscritti,
       h.max_iscritti,
       COUNT(DISTINCT t.team_id)     AS totale_team,
       COUNT(DISTINCT gh.giudice_id) AS totale_giudici,
       COALESCE(AVG(v.valore), 0)    AS media_voti_generale
FROM hackathon h
         JOIN hackathon_status hs ON h.status_id = hs.status_id
         JOIN utenti u_org ON h.organizzatore_id = u_org.utente_id
         LEFT JOIN registrazioni r ON r.hackathon_id = h.hackathon_id
         LEFT JOIN team t ON t.hackathon_id = h.hackathon_id
         LEFT JOIN giudici_hackathon gh ON gh.hackathon_id = h.hackathon_id AND gh.stato_invito_id =
                                                                                (SELECT status_id FROM invito_status WHERE status_name = 'ACCEPTED')
         LEFT JOIN voti v ON v.hackathon_id = h.hackathon_id
GROUP BY h.hackathon_id, h.titolo, h.sede, h.data_inizio, h.data_fine, hs.status_name, h.max_iscritti, u_org.username;

-- Vista utenti per tipo
CREATE VIEW utenti_per_tipo AS
SELECT ur.role_name                 AS tipo_utente,
       COUNT(u.utente_id)           AS numero_utenti,
       STRING_AGG(u.username, ', ') AS lista_utenti
FROM user_roles ur
         LEFT JOIN utenti u ON u.tipo_utente_id = ur.role_id
GROUP BY ur.role_id, ur.role_name
ORDER BY ur.role_name;

-- ==================================================
-- DATI DI ESEMPIO PER TESTING
-- ==================================================

-- Inserimento utenti di esempio con diversi tipi
INSERT INTO utenti (username, email, password, nome, cognome, tipo_utente_id)
VALUES ('mario.organizzatore', 'mario.org@email.com', '123', 'Mario', 'Rossi',
        (SELECT role_id FROM user_roles WHERE role_name = 'ORGANIZZATORE')),
       ('giulia.giudice', 'giulia.giudice@email.com', '456', 'Giulia', 'Verdi',
        (SELECT role_id FROM user_roles WHERE role_name = 'GIUDICE')),
       ('luca.partecipante', 'luca.part@email.com', '789', 'Luca', 'Bianchi',
        (SELECT role_id FROM user_roles WHERE role_name = 'PARTECIPANTE')),
       ('anna.partecipante', 'anna.part@email.com', '101', 'Anna', 'Neri',
        (SELECT role_id FROM user_roles WHERE role_name = 'PARTECIPANTE')),
       ('marco.giudice', 'marco.giudice@email.com', '202', 'Marco', 'Blu',
        (SELECT role_id FROM user_roles WHERE role_name = 'GIUDICE'));

-- Inserimento hackathon di esempio
INSERT INTO hackathon (titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_registrazioni,
                       max_iscritti, max_dimensione_team, organizzatore_id, status_id)
VALUES ('AI Innovation Challenge 2024',
        'Hackathon dedicato all''innovazione nell''intelligenza artificiale',
        'Milano - Università Bocconi',
        '2024-06-15 09:00:00',
        '2024-06-16 18:00:00',
        '2024-06-13 23:59:59',
        100,
        4,
        (SELECT utente_id FROM utenti WHERE username = 'mario.organizzatore'),
        (SELECT status_id FROM hackathon_status WHERE status_name = 'REGISTRAZIONI_APERTE'));

-- ==================================================
-- QUERY DI VERIFICA
-- ==================================================

-- Verifica distribuzione utenti per tipo
SELECT *
FROM utenti_per_tipo;

-- Verifica organizzatori e loro hackathon
SELECT u.username, u.nome, u.cognome, COUNT(h.hackathon_id) as hackathon_creati
FROM utenti u
         JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
         LEFT JOIN hackathon h ON h.organizzatore_id = u.utente_id
WHERE ur.role_name = 'ORGANIZZATORE'
GROUP BY u.utente_id, u.username, u.nome, u.cognome;

-- Verifica vincoli di ruolo
-- Questa query dovrebbe fallire se eseguita (utente non organizzatore che crea hackathon)
-- INSERT INTO hackathon (titolo, sede, data_inizio, data_fine, data_chiusura_registrazioni, max_iscritti, max_dimensione_team, organizzatore_id, status_id) 
-- VALUES ('Test', 'Test', '2024-07-01 09:00:00', '2024-07-02 18:00:00', '2024-06-29 23:59:59', 50, 3, 
--         (SELECT utente_id FROM utenti WHERE username = 'luca.partecipante'), 1);