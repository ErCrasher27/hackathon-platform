-- ==================================================
-- HACKATHON DATABASE
-- ==================================================

-- ==================================================
-- TABELLE ENUM
-- ==================================================

CREATE TABLE ruoli_utente
(
    ruolo_id   INTEGER PRIMARY KEY,
    nome_ruolo VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE stati_hackathon
(
    stato_id   INTEGER PRIMARY KEY,
    nome_stato VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE stati_invito
(
    stato_id   INTEGER PRIMARY KEY,
    nome_stato VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE ruoli_team
(
    ruolo_id   INTEGER PRIMARY KEY,
    nome_ruolo VARCHAR(20) NOT NULL UNIQUE
);

-- ==================================================
-- TABELLE PRINCIPALI
-- ==================================================

-- Utenti del sistema
CREATE TABLE utenti
(
    utente_id             SERIAL PRIMARY KEY,
    username              VARCHAR(50)  NOT NULL UNIQUE,
    email                 VARCHAR(100) NOT NULL UNIQUE,
    password              VARCHAR(255) NOT NULL,
    nome                  VARCHAR(50)  NOT NULL,
    cognome               VARCHAR(50)  NOT NULL,
    ruolo_fk_ruoli_utente INTEGER      NOT NULL REFERENCES ruoli_utente (ruolo_id),
    data_registrazione    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_email_valida CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
) ,
    CONSTRAINT chk_username_lunghezza
        CHECK (LENGTH(username) >= 3),
    CONSTRAINT chk_password_lunghezza
        CHECK (LENGTH(password) >= 6)
);

-- Eventi hackathon
CREATE TABLE hackathons
(
    hackathon_id             SERIAL PRIMARY KEY,
    titolo                   VARCHAR(200) NOT NULL,
    descrizione              TEXT,
    sede                     VARCHAR(200) NOT NULL,
    data_inizio              TIMESTAMP    NOT NULL,
    data_fine                TIMESTAMP    NOT NULL,
    data_chiusura_reg        TIMESTAMP    NOT NULL,
    max_iscritti             INTEGER      NOT NULL CHECK (max_iscritti > 0),
    max_membri_team          INTEGER      NOT NULL CHECK (max_membri_team >= 2),
    organizzatore_fk_utenti  INTEGER      NOT NULL REFERENCES utenti (utente_id),
    stato_fk_stati_hackathon INTEGER      NOT NULL REFERENCES stati_hackathon (stato_id) DEFAULT 1,
    data_creazione           TIMESTAMP                                                   DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_ordine_date CHECK (data_inizio < data_fine),
    CONSTRAINT chk_deadline_registrazione CHECK (data_chiusura_reg <= data_inizio)
);

-- Teams partecipanti
CREATE TABLE teams
(
    team_id                 SERIAL PRIMARY KEY,
    nome                    VARCHAR(100) NOT NULL,
    hackathon_fk_hackathons INTEGER      NOT NULL REFERENCES hackathons (hackathon_id) ON DELETE CASCADE,
    definitivo              BOOLEAN   DEFAULT FALSE,
    data_creazione          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_nome_team_per_hackathon UNIQUE (nome, hackathon_fk_hackathons)
);

-- Registrazioni e appartenenza team
CREATE TABLE registrazioni
(
    registrazione_id        SERIAL PRIMARY KEY,
    partecipante_fk_utenti  INTEGER NOT NULL REFERENCES utenti (utente_id),
    hackathon_fk_hackathons INTEGER NOT NULL REFERENCES hackathons (hackathon_id) ON DELETE CASCADE,
    team_fk_teams           INTEGER REFERENCES teams (team_id) ON DELETE SET NULL,
    ruolo_fk_ruoli_team     INTEGER REFERENCES ruoli_team (ruolo_id),
    data_registrazione      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_ingresso_team      TIMESTAMP,
    CONSTRAINT uq_utente_per_hackathon UNIQUE (partecipante_fk_utenti, hackathon_fk_hackathons),
    CONSTRAINT chk_ruolo_con_team CHECK ((team_fk_teams IS NULL AND ruolo_fk_ruoli_team IS NULL) OR
                                         (team_fk_teams IS NOT NULL AND ruolo_fk_ruoli_team IS NOT NULL))
);

-- Giudici assegnati agli hackathon
CREATE TABLE giudici_hackathon
(
    giudice_hackathon_id    SERIAL PRIMARY KEY,
    hackathon_fk_hackathons INTEGER NOT NULL REFERENCES hackathons (hackathon_id) ON DELETE CASCADE,
    giudice_fk_utenti       INTEGER NOT NULL REFERENCES utenti (utente_id),
    data_assegnazione       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_giudice_per_hackathon UNIQUE (hackathon_fk_hackathons, giudice_fk_utenti)
);

-- Problemi proposti dai giudici
CREATE TABLE problemi
(
    problema_id                       SERIAL PRIMARY KEY,
    giudice_hack_fk_giudici_hackathon INTEGER      NOT NULL REFERENCES giudici_hackathon (giudice_hackathon_id) ON DELETE CASCADE,
    titolo                            VARCHAR(200) NOT NULL,
    descrizione                       TEXT         NOT NULL,
    data_pubblicazione                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Documenti di progresso
CREATE TABLE progressi
(
    progresso_id                   SERIAL PRIMARY KEY,
    registrazione_fk_registrazioni INTEGER NOT NULL REFERENCES registrazioni (registrazione_id) ON DELETE CASCADE,
    documento_path                 VARCHAR(500),
    documento_nome                 VARCHAR(200),
    data_caricamento               TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feedback sui progressi
CREATE TABLE commenti
(
    commento_id                       SERIAL PRIMARY KEY,
    progresso_fk_progressi            INTEGER NOT NULL REFERENCES progressi (progresso_id) ON DELETE CASCADE,
    giudice_hack_fk_giudici_hackathon INTEGER NOT NULL REFERENCES giudici_hackathon (giudice_hackathon_id),
    testo                             TEXT    NOT NULL,
    data_commento                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Valutazioni dei team
CREATE TABLE voti
(
    voto_id                           SERIAL PRIMARY KEY,
    team_fk_teams                     INTEGER NOT NULL REFERENCES teams (team_id) ON DELETE CASCADE,
    giudice_hack_fk_giudici_hackathon INTEGER NOT NULL REFERENCES giudici_hackathon (giudice_hackathon_id),
    valore                            INTEGER NOT NULL CHECK (valore >= 1 AND valore <= 10),
    data_voto                         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_voto_per_giudice_team UNIQUE (team_fk_teams, giudice_hack_fk_giudici_hackathon)
);

-- Inviti per unirsi ai team
CREATE TABLE inviti_team
(
    invito_id                      SERIAL PRIMARY KEY,
    invitante_reg_fk_registrazioni INTEGER NOT NULL REFERENCES registrazioni (registrazione_id) ON DELETE CASCADE,
    invitato_fk_utenti             INTEGER NOT NULL REFERENCES utenti (utente_id) ON DELETE CASCADE,
    messaggio                      TEXT,
    stato_fk_stati_invito          INTEGER   DEFAULT 1 REFERENCES stati_invito (stato_id),
    data_invito                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_invito_team UNIQUE (invitante_reg_fk_registrazioni, invitato_fk_utenti)
);

-- Inviti per giudicare
CREATE TABLE inviti_giudice
(
    invito_id               SERIAL PRIMARY KEY,
    invitante_fk_utenti     INTEGER NOT NULL REFERENCES utenti (utente_id) ON DELETE CASCADE,
    invitato_fk_utenti      INTEGER NOT NULL REFERENCES utenti (utente_id) ON DELETE CASCADE,
    hackathon_fk_hackathons INTEGER NOT NULL REFERENCES hackathons (hackathon_id) ON DELETE CASCADE,
    stato_fk_stati_invito   INTEGER   DEFAULT 1 REFERENCES stati_invito (stato_id),
    data_invito             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_invito_giudice UNIQUE (invitante_fk_utenti, invitato_fk_utenti, hackathon_fk_hackathons),
    CONSTRAINT chk_no_auto_invito CHECK (invitante_fk_utenti != invitato_fk_utenti
) );

-- ==================================================
-- DATI ENUM
-- ==================================================

INSERT INTO ruoli_utente (ruolo_id, nome_ruolo)
VALUES (1, 'ORGANIZZATORE'),
       (2, 'GIUDICE'),
       (3, 'PARTECIPANTE');

INSERT INTO stati_hackathon (stato_id, nome_stato)
VALUES (1, 'REGISTRAZIONI_APERTE'),
       (2, 'REGISTRAZIONI_CHIUSE'),
       (3, 'IN_CORSO'),
       (4, 'TERMINATO');

INSERT INTO stati_invito (stato_id, nome_stato)
VALUES (1, 'PENDING'),
       (2, 'ACCEPTED'),
       (3, 'DECLINED');

INSERT INTO ruoli_team (ruolo_id, nome_ruolo)
VALUES (1, 'LEADER'),
       (2, 'MEMBRO');

-- ==================================================
-- TRIGGER VALIDAZIONE RUOLI
-- ==================================================

-- Solo organizzatori possono creare hackathon
CREATE
OR REPLACE FUNCTION check_organizzatore_role()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti
        WHERE utente_id = NEW.organizzatore_fk_utenti
        AND ruolo_fk_ruoli_utente = 1
    ) THEN
        RAISE EXCEPTION 'Solo gli organizzatori possono creare hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_01_verifica_organizzatore
    BEFORE INSERT OR
UPDATE ON hackathons FOR EACH ROW EXECUTE FUNCTION check_organizzatore_role();

-- Solo utenti con ruolo giudice possono essere assegnati
CREATE
OR REPLACE FUNCTION check_giudice_role()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti
        WHERE utente_id = NEW.giudice_fk_utenti
        AND ruolo_fk_ruoli_utente = 2
    ) THEN
        RAISE EXCEPTION 'Solo utenti con ruolo GIUDICE possono essere assegnati';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_02_verifica_giudice
    BEFORE INSERT OR
UPDATE ON giudici_hackathon FOR EACH ROW EXECUTE FUNCTION check_giudice_role();

-- ==================================================
-- TRIGGER GESTIONE TEAM
-- ==================================================

-- Un solo leader per team
CREATE
OR REPLACE FUNCTION check_single_leader()
RETURNS TRIGGER AS $$
BEGIN
    IF
NEW.ruolo_fk_ruoli_team = 1 AND EXISTS (
        SELECT 1 FROM registrazioni
        WHERE team_fk_teams = NEW.team_fk_teams
        AND ruolo_fk_ruoli_team = 1
        AND registrazione_id != COALESCE(NEW.registrazione_id, -1)
    ) THEN
        RAISE EXCEPTION 'Un team può avere un solo leader';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_03_unico_leader
    BEFORE INSERT OR
UPDATE ON registrazioni FOR EACH ROW EXECUTE FUNCTION check_single_leader();

-- Verifica dimensione massima team
CREATE
OR REPLACE FUNCTION check_team_size()
RETURNS TRIGGER AS $$
DECLARE
v_membri_attuali INTEGER;
    v_max_membri
INTEGER;
BEGIN
    IF
NEW.team_fk_teams IS NOT NULL AND
       (TG_OP = 'INSERT' OR NEW.team_fk_teams IS DISTINCT FROM OLD.team_fk_teams) THEN
SELECT COUNT(*), h.max_membri_team
INTO v_membri_attuali, v_max_membri
FROM registrazioni r
         JOIN teams t ON t.team_id = r.team_fk_teams
         JOIN hackathons h ON h.hackathon_id = t.hackathon_fk_hackathons
WHERE r.team_fk_teams = NEW.team_fk_teams
GROUP BY h.max_membri_team;
IF
v_membri_attuali >= v_max_membri THEN
            RAISE EXCEPTION 'Team ha raggiunto la dimensione massima di % membri', v_max_membri;
END IF;
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_04_dimensione_team
    BEFORE INSERT OR
UPDATE ON registrazioni FOR EACH ROW EXECUTE FUNCTION check_team_size();

-- Solo membri con team possono caricare progressi
CREATE
OR REPLACE FUNCTION check_team_member_progress()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM registrazioni
        WHERE registrazione_id = NEW.registrazione_fk_registrazioni
        AND team_fk_teams IS NOT NULL
    ) THEN
        RAISE EXCEPTION 'Solo membri di un team possono caricare progressi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_05_membro_team_progresso
    BEFORE INSERT
    ON progressi
    FOR EACH ROW EXECUTE FUNCTION check_team_member_progress();

-- ==================================================
-- TRIGGER CONTROLLO STATO HACKATHON
-- ==================================================

-- No registrazioni dopo la deadline
CREATE
OR REPLACE FUNCTION check_registration_deadline()
RETURNS TRIGGER AS $$
BEGIN
    IF
EXISTS (
        SELECT 1 FROM hackathons
        WHERE hackathon_id = NEW.hackathon_fk_hackathons
        AND data_chiusura_reg < CURRENT_TIMESTAMP
    ) THEN
        RAISE EXCEPTION 'Registrazioni chiuse per questo hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_06_deadline_registrazione
    BEFORE INSERT
    ON registrazioni
    FOR EACH ROW EXECUTE FUNCTION check_registration_deadline();

-- Verifica limite iscritti
CREATE
OR REPLACE FUNCTION check_max_participants()
RETURNS TRIGGER AS $$
DECLARE
v_iscritti_attuali INTEGER;
    v_max_iscritti
INTEGER;
BEGIN
SELECT COUNT(*), h.max_iscritti
INTO v_iscritti_attuali, v_max_iscritti
FROM registrazioni r
         JOIN hackathons h ON h.hackathon_id = r.hackathon_fk_hackathons
WHERE r.hackathon_fk_hackathons = NEW.hackathon_fk_hackathons
GROUP BY h.max_iscritti;
IF
v_iscritti_attuali >= v_max_iscritti THEN
        RAISE EXCEPTION 'Raggiunto numero massimo iscritti';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_07_max_partecipanti
    BEFORE INSERT
    ON registrazioni
    FOR EACH ROW EXECUTE FUNCTION check_max_participants();

-- Solo team definitivi possono essere votati
CREATE
OR REPLACE FUNCTION check_team_definitivo()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM teams
        WHERE team_id = NEW.team_fk_teams
        AND definitivo = TRUE
    ) THEN
        RAISE EXCEPTION 'Si possono votare solo team definitivi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_08_team_definitivo_voto
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_team_definitivo();

-- Voti solo durante o dopo hackathon
CREATE
OR REPLACE FUNCTION check_hackathon_status_vote()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM teams t
        JOIN hackathons h ON h.hackathon_id = t.hackathon_fk_hackathons
        WHERE t.team_id = NEW.team_fk_teams
        AND h.stato_fk_stati_hackathon IN (3, 4)
    ) THEN
        RAISE EXCEPTION 'Voti permessi solo durante o dopo hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_09_stato_hackathon_voto
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_status_vote();

-- Problemi solo durante hackathon
CREATE
OR REPLACE FUNCTION check_hackathon_in_corso()
RETURNS TRIGGER AS $$
DECLARE
v_hackathon_id INTEGER;
BEGIN
SELECT hackathon_fk_hackathons
INTO v_hackathon_id
FROM giudici_hackathon
WHERE giudice_hackathon_id = COALESCE(NEW.giudice_hack_fk_giudici_hackathon, OLD.giudice_hack_fk_giudici_hackathon);
IF
NOT EXISTS (
        SELECT 1 FROM hackathons
        WHERE hackathon_id = v_hackathon_id
        AND stato_fk_stati_hackathon = 3
    ) THEN
        RAISE EXCEPTION 'Operazione permessa solo durante hackathon in corso';
END IF;
RETURN COALESCE(NEW, OLD);
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_10_problema_in_corso
    BEFORE INSERT OR
UPDATE OR
DELETE
ON problemi FOR EACH ROW EXECUTE FUNCTION check_hackathon_in_corso();

-- ==================================================
-- TRIGGER INVITI
-- ==================================================

-- Solo leader possono invitare al team
CREATE
OR REPLACE FUNCTION check_leader_invite()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM registrazioni
        WHERE registrazione_id = NEW.invitante_reg_fk_registrazioni
        AND ruolo_fk_ruoli_team = 1
    ) THEN
        RAISE EXCEPTION 'Solo il leader può invitare membri';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_11_leader_invita
    BEFORE INSERT
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION check_leader_invite();

-- Auto-join team su accettazione invito
CREATE
OR REPLACE FUNCTION auto_join_team()
RETURNS TRIGGER AS $$
DECLARE
v_team_id INTEGER;
    v_hackathon_id
INTEGER;
BEGIN
    IF
NEW.stato_fk_stati_invito = 2 AND OLD.stato_fk_stati_invito = 1 THEN
SELECT r.team_fk_teams, r.hackathon_fk_hackathons
INTO v_team_id, v_hackathon_id
FROM registrazioni r
WHERE r.registrazione_id = NEW.invitante_reg_fk_registrazioni;
UPDATE registrazioni
SET team_fk_teams       = v_team_id,
    ruolo_fk_ruoli_team = 2,
    data_ingresso_team  = CURRENT_TIMESTAMP
WHERE partecipante_fk_utenti = NEW.invitato_fk_utenti
  AND hackathon_fk_hackathons = v_hackathon_id;
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_12_auto_join_team
    AFTER UPDATE
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION auto_join_team();

-- ==================================================
-- VIEWS
-- ==================================================

-- Classifica team per hackathon
CREATE VIEW v_classifica_hackathons AS
SELECT h.hackathon_id,
       h.titolo                                 AS hackathon,
       t.team_id,
       t.nome                                   AS team,
       AVG(v.valore)                            AS media_voti,
       COUNT(DISTINCT v.voto_id)                AS num_voti,
       COUNT(DISTINCT r.partecipante_fk_utenti) AS num_membri
FROM hackathons h
         JOIN teams t ON t.hackathon_fk_hackathons = h.hackathon_id
         LEFT JOIN voti v ON v.team_fk_teams = t.team_id
         LEFT JOIN registrazioni r ON r.team_fk_teams = t.team_id
WHERE t.definitivo = TRUE
GROUP BY h.hackathon_id, h.titolo, t.team_id, t.nome
ORDER BY h.hackathon_id, AVG(v.valore) DESC NULLS LAST;