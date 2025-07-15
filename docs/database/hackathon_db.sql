-- ==================================================
-- HACKATHON DATABASE
-- ==================================================

-- ==================================================
-- SEZIONE 1: TABELLE ENUM
-- ==================================================

CREATE TABLE user_roles
(
    role_id   INTEGER PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE hackathon_status
(
    status_id   INTEGER PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE invito_status
(
    status_id   INTEGER PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

-- Popolamento tabelle enum
INSERT INTO user_roles (role_id, role_name)
VALUES (1, 'ORGANIZZATORE'),
       (2, 'GIUDICE'),
       (3, 'PARTECIPANTE');

INSERT INTO hackathon_status (status_id, status_name)
VALUES (1, 'REGISTRAZIONI_APERTE'),
       (2, 'REGISTRAZIONI_CHIUSE'),
       (3, 'IN_CORSO'),
       (4, 'TERMINATO');

INSERT INTO invito_status (status_id, status_name)
VALUES (1, 'PENDING'),
       (2, 'ACCEPTED'),
       (3, 'DECLINED');

-- ==================================================
-- SEZIONE 2: TABELLE PRINCIPALI
-- ==================================================

-- Tabella utenti
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

    -- Vincolo formato email
    CONSTRAINT check_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
) ,
    -- Vincolo lunghezza username
    CONSTRAINT check_username_length CHECK (LENGTH(username) >= 3),
    -- Vincolo lunghezza password
    CONSTRAINT check_password_length CHECK (LENGTH(password) >= 6)
);

-- Tabella hackathon
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
    status_id                   INTEGER      NOT NULL REFERENCES hackathon_status (status_id) DEFAULT 1,
    data_creazione              TIMESTAMP                                                     DEFAULT CURRENT_TIMESTAMP,

    -- Vincoli temporali
    CONSTRAINT check_date_order CHECK (data_inizio < data_fine),
    CONSTRAINT check_registration_deadline CHECK (data_chiusura_registrazioni <= data_inizio),
    -- Vincolo dimensione team minima
    CONSTRAINT check_min_team_size CHECK (max_dimensione_team >= 2)
);

-- Tabella team
CREATE TABLE team
(
    team_id        SERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    hackathon_id   INTEGER      NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    definitivo     BOOLEAN   DEFAULT FALSE,

    -- Ogni team ha nome univoco per hackathon
    UNIQUE (nome, hackathon_id)
);

-- Tabella problema
CREATE TABLE problema
(
    problema_id        SERIAL PRIMARY KEY,
    hackathon_id       INTEGER      NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    titolo             VARCHAR(200) NOT NULL,
    descrizione        TEXT         NOT NULL,
    data_pubblicazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pubblicato_da      INTEGER      NOT NULL REFERENCES utenti (utente_id)
);

-- ==================================================
-- SEZIONE 3: TABELLE DI RELAZIONE
-- ==================================================

-- Registrazioni utenti agli hackathon
CREATE TABLE registrazioni
(
    registrazione_id   SERIAL PRIMARY KEY,
    utente_id          INTEGER NOT NULL REFERENCES utenti (utente_id),
    hackathon_id       INTEGER NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    data_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    team_id            INTEGER REFERENCES team (team_id) ON DELETE SET NULL,

    -- Un utente può registrarsi una sola volta per hackathon
    UNIQUE (utente_id, hackathon_id)
);

-- Giudici assegnati agli hackathon
CREATE TABLE giudici_hackathon
(
    giudice_hackathon_id SERIAL PRIMARY KEY,
    hackathon_id         INTEGER NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    giudice_id           INTEGER NOT NULL REFERENCES utenti (utente_id),
    data_invito          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    invitato_da          INTEGER NOT NULL REFERENCES utenti (utente_id),
    stato_invito_id      INTEGER   DEFAULT 1 REFERENCES invito_status (status_id),

    -- Un giudice può essere invitato una sola volta per hackathon
    UNIQUE (hackathon_id, giudice_id)
);

-- Membri dei team
CREATE TABLE membri_team
(
    membro_team_id SERIAL PRIMARY KEY,
    team_id        INTEGER NOT NULL REFERENCES team (team_id) ON DELETE CASCADE,
    utente_id      INTEGER NOT NULL REFERENCES utenti (utente_id),
    data_ingresso  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    ruolo_team     VARCHAR(20) DEFAULT 'MEMBRO' CHECK (ruolo_team IN ('LEADER', 'MEMBRO')),

    -- Un utente può essere in un solo team
    UNIQUE (team_id, utente_id)
);

-- Progressi caricati dai team
CREATE TABLE progressi
(
    progresso_id     SERIAL PRIMARY KEY,
    team_id          INTEGER NOT NULL REFERENCES team (team_id) ON DELETE CASCADE,
    documento_path   VARCHAR(500),
    documento_nome   VARCHAR(200),
    data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    caricato_da      INTEGER NOT NULL REFERENCES utenti (utente_id)
);

-- Commenti dei giudici sui progressi
CREATE TABLE commenti
(
    commento_id   SERIAL PRIMARY KEY,
    progresso_id  INTEGER NOT NULL REFERENCES progressi (progresso_id) ON DELETE CASCADE,
    giudice_id    INTEGER NOT NULL REFERENCES utenti (utente_id),
    testo         TEXT    NOT NULL,
    data_commento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Voti dei giudici ai team
CREATE TABLE voti
(
    voto_id      SERIAL PRIMARY KEY,
    hackathon_id INTEGER NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    team_id      INTEGER NOT NULL REFERENCES team (team_id) ON DELETE CASCADE,
    giudice_id   INTEGER NOT NULL REFERENCES utenti (utente_id),
    valore       INTEGER NOT NULL CHECK (valore >= 1 AND valore <= 10),
    data_voto    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Un giudice può votare un team una sola volta per hackathon
    UNIQUE (hackathon_id, team_id, giudice_id)
);

-- Inviti per unirsi ai team
CREATE TABLE inviti_team
(
    invito_id               SERIAL PRIMARY KEY,
    team_id                 INTEGER NOT NULL REFERENCES team (team_id) ON DELETE CASCADE,
    invitante_id            INTEGER NOT NULL REFERENCES utenti (utente_id),
    invitato_id             INTEGER NOT NULL REFERENCES utenti (utente_id),
    messaggio_motivazionale TEXT,
    stato_invito_id         INTEGER   DEFAULT 1 REFERENCES invito_status (status_id),
    data_invito             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_risposta           TIMESTAMP,

    -- Un invito non può essere duplicato
    UNIQUE (team_id, invitato_id),
    -- Non si può invitare se stessi
    CONSTRAINT check_self_invite CHECK (invitante_id != invitato_id
) );

-- ==================================================
-- SEZIONE 4: TRIGGER PER CONTROLLI DI BUSINESS LOGIC
-- ==================================================

-- Verifica che solo organizzatori possano creare hackathon
CREATE
OR REPLACE FUNCTION check_organizzatore_role()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti u
        WHERE u.utente_id = NEW.organizzatore_id
        AND u.tipo_utente_id = 1  -- ORGANIZZATORE
    ) THEN
        RAISE EXCEPTION 'Solo gli organizzatori possono creare hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_organizzatore_role
    BEFORE INSERT OR
UPDATE ON hackathon FOR EACH ROW EXECUTE FUNCTION check_organizzatore_role();

-- Verifica che solo giudici possano essere invitati come giudici
CREATE
OR REPLACE FUNCTION check_giudice_role()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti u
        WHERE u.utente_id = NEW.giudice_id
        AND u.tipo_utente_id = 2  -- GIUDICE
    ) THEN
        RAISE EXCEPTION 'Solo gli utenti con ruolo GIUDICE possono essere invitati come giudici';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_giudice_role
    BEFORE INSERT OR
UPDATE ON giudici_hackathon FOR EACH ROW EXECUTE FUNCTION check_giudice_role();

-- Verifica che non si possano invitare giudici se l'hackathon è terminato
CREATE
OR REPLACE FUNCTION check_hackathon_not_terminated_for_judge_invite()
RETURNS TRIGGER AS $$
BEGIN
    IF
EXISTS (
        SELECT 1 FROM hackathon h
        WHERE h.hackathon_id = NEW.hackathon_id
        AND h.status_id = 4  -- TERMINATO
    ) THEN
        RAISE EXCEPTION 'Non è possibile invitare giudici per un hackathon terminato';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_hackathon_not_terminated_for_judge_invite
    BEFORE INSERT
    ON giudici_hackathon
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_judge_invite();

-- Verifica che non si possano rimuovere inviti giudice se hanno accettato
CREATE
OR REPLACE FUNCTION check_judge_invite_not_accepted_for_removal()
RETURNS TRIGGER AS $$
BEGIN
    IF
OLD.stato_invito_id = 2 THEN  -- ACCEPTED
        RAISE EXCEPTION 'Non è possibile rimuovere un invito giudice che è stato accettato';
END IF;
RETURN OLD;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_judge_invite_not_accepted_for_removal
    BEFORE DELETE
    ON giudici_hackathon
    FOR EACH ROW EXECUTE FUNCTION check_judge_invite_not_accepted_for_removal();

-- Verifica che un utente sia in un solo team per hackathon
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

CREATE TRIGGER trigger_single_team_per_hackathon
    BEFORE INSERT OR
UPDATE ON membri_team FOR EACH ROW EXECUTE FUNCTION check_single_team_per_hackathon();

-- Verifica limiti dimensione team
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

CREATE TRIGGER trigger_team_size_limit
    BEFORE INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION check_team_size_limit();

-- Verifica che l'utente sia registrato all'hackathon prima di unirsi a un team
CREATE
OR REPLACE FUNCTION check_registration_before_team()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal team
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = NEW.team_id;

-- Verifica che l'utente sia registrato
IF
NOT EXISTS (
        SELECT 1
        FROM registrazioni
        WHERE utente_id = NEW.utente_id
        AND hackathon_id = hack_id
    ) THEN
        RAISE EXCEPTION 'L''utente deve essere registrato all''hackathon prima di unirsi a un team';
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_registration_before_team
    BEFORE INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION check_registration_before_team();

-- Aggiorna automaticamente il team_id in registrazioni quando un utente si unisce a un team
CREATE
OR REPLACE FUNCTION update_registration_team()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal team
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = NEW.team_id;

-- Aggiorna la registrazione
UPDATE registrazioni
SET team_id = NEW.team_id
WHERE utente_id = NEW.utente_id
  AND hackathon_id = hack_id;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_registration_team
    AFTER INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION update_registration_team();

-- Verifica che solo membri del team possano caricare progressi
CREATE
OR REPLACE FUNCTION check_team_member_progress()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1
        FROM membri_team
        WHERE team_id = NEW.team_id
        AND utente_id = NEW.caricato_da
    ) THEN
        RAISE EXCEPTION 'Solo i membri del team possono caricare progressi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_team_member_progress
    BEFORE INSERT
    ON progressi
    FOR EACH ROW EXECUTE FUNCTION check_team_member_progress();

-- Verifica che solo giudici accettati possano commentare
CREATE
OR REPLACE FUNCTION check_judge_can_comment()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal progresso
SELECT h.hackathon_id
INTO hack_id
FROM progressi p
         JOIN team t ON t.team_id = p.team_id
         JOIN hackathon h ON h.hackathon_id = t.hackathon_id
WHERE p.progresso_id = NEW.progresso_id;

-- Verifica che sia un giudice accettato
IF
NOT EXISTS (
        SELECT 1
        FROM giudici_hackathon
        WHERE hackathon_id = hack_id
        AND giudice_id = NEW.giudice_id
        AND stato_invito_id = 2  -- ACCEPTED
    ) THEN
        RAISE EXCEPTION 'Solo i giudici accettati possono commentare i progressi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_judge_can_comment
    BEFORE INSERT
    ON commenti
    FOR EACH ROW EXECUTE FUNCTION check_judge_can_comment();

-- Verifica che solo giudici accettati possano votare
CREATE
OR REPLACE FUNCTION check_judge_can_vote()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1
        FROM giudici_hackathon
        WHERE hackathon_id = NEW.hackathon_id
        AND giudice_id = NEW.giudice_id
        AND stato_invito_id = 2  -- ACCEPTED
    ) THEN
        RAISE EXCEPTION 'Solo i giudici accettati possono votare';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_judge_can_vote
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_judge_can_vote();

-- Verifica che il team sia definitivo prima di poter essere votato
CREATE
OR REPLACE FUNCTION check_team_definitivo_for_vote()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1
        FROM team
        WHERE team_id = NEW.team_id
        AND definitivo = TRUE
    ) THEN
        RAISE EXCEPTION 'Si possono votare solo team definitivi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_team_definitivo_for_vote
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_team_definitivo_for_vote();

-- Verifica che l'hackathon sia in corso per poter votare
CREATE
OR REPLACE FUNCTION check_hackathon_status_for_vote()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1
        FROM hackathon
        WHERE hackathon_id = NEW.hackathon_id
        AND status_id IN (3, 4)  -- IN_CORSO o TERMINATO
    ) THEN
        RAISE EXCEPTION 'Si può votare solo durante o dopo l''hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_hackathon_status_for_vote
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_status_for_vote();

-- Verifica che non si possa modificare un team definitivo
CREATE
OR REPLACE FUNCTION prevent_definitivo_team_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF
TG_OP = 'INSERT' THEN
        IF EXISTS (
            SELECT 1
            FROM team
            WHERE team_id = NEW.team_id
            AND definitivo = TRUE
        ) THEN
            RAISE EXCEPTION 'Non si possono modificare team definitivi';
END IF;
RETURN NEW;
END IF;

    IF
TG_OP = 'DELETE' THEN
        IF EXISTS (
            SELECT 1
            FROM team
            WHERE team_id = OLD.team_id
            AND definitivo = TRUE
        ) THEN
            RAISE EXCEPTION 'Non si possono modificare team definitivi';
END IF;
RETURN OLD;
END IF;

RETURN NULL;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_definitivo_team_add
    BEFORE INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION prevent_definitivo_team_changes();

CREATE TRIGGER trigger_prevent_definitivo_team_delete
    BEFORE DELETE
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION prevent_definitivo_team_changes();

-- Verifica che si possano gestire problemi solo se l'hackathon è in corso
CREATE
OR REPLACE FUNCTION check_hackathon_in_corso_for_problema()
RETURNS TRIGGER AS $$
BEGIN
    -- Per INSERT e UPDATE, controlla NEW
    IF
TG_OP IN ('INSERT', 'UPDATE') THEN
        IF NOT EXISTS (
            SELECT 1 FROM hackathon h
            WHERE h.hackathon_id = NEW.hackathon_id
            AND h.status_id = 3  -- IN_CORSO
        ) THEN
            RAISE EXCEPTION 'I problemi possono essere gestiti solo quando l''hackathon è in corso';
END IF;
RETURN NEW;
END IF;

    -- Per DELETE, controlla OLD
    IF
TG_OP = 'DELETE' THEN
        IF NOT EXISTS (
            SELECT 1 FROM hackathon h
            WHERE h.hackathon_id = OLD.hackathon_id
            AND h.status_id = 3  -- IN_CORSO
        ) THEN
            RAISE EXCEPTION 'I problemi possono essere rimossi solo quando l''hackathon è in corso';
END IF;
RETURN OLD;
END IF;

RETURN NULL;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_hackathon_in_corso_for_problema_insert
    BEFORE INSERT
    ON problema
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_in_corso_for_problema();

CREATE TRIGGER trigger_hackathon_in_corso_for_problema_delete
    BEFORE DELETE
    ON problema
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_in_corso_for_problema();

-- ==================================================
-- SEZIONE 5: TRIGGER PER CONTROLLI AGGIUNTIVI (SOLO BACKUP LAYER)
-- ==================================================

-- Verifica che non si possano registrare utenti dopo la chiusura registrazioni
CREATE
OR REPLACE FUNCTION check_registration_deadline()
RETURNS TRIGGER AS $$
BEGIN
    IF
EXISTS (
        SELECT 1
        FROM hackathon
        WHERE hackathon_id = NEW.hackathon_id
        AND data_chiusura_registrazioni < CURRENT_TIMESTAMP
    ) THEN
        RAISE EXCEPTION 'Le registrazioni per questo hackathon sono chiuse';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_registration_deadline
    BEFORE INSERT
    ON registrazioni
    FOR EACH ROW EXECUTE FUNCTION check_registration_deadline();

-- Verifica limite iscritti hackathon
CREATE
OR REPLACE FUNCTION check_hackathon_max_participants()
RETURNS TRIGGER AS $$
DECLARE
current_count INTEGER;
    max_count
INTEGER;
BEGIN
SELECT COUNT(*), h.max_iscritti
INTO current_count, max_count
FROM registrazioni r
         JOIN hackathon h ON h.hackathon_id = r.hackathon_id
WHERE r.hackathon_id = NEW.hackathon_id
GROUP BY h.max_iscritti;

IF
current_count >= max_count THEN
        RAISE EXCEPTION 'Raggiunto il numero massimo di iscritti per questo hackathon';
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_max_participants
    BEFORE INSERT
    ON registrazioni
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_max_participants();

-- Verifica che solo il leader del team possa invitare membri
CREATE
OR REPLACE FUNCTION check_team_leader_invite()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1
        FROM membri_team
        WHERE team_id = NEW.team_id
        AND utente_id = NEW.invitante_id
        AND ruolo_team = 'LEADER'
    ) THEN
        RAISE EXCEPTION 'Solo il leader del team può invitare nuovi membri';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_team_leader_invite
    BEFORE INSERT
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION check_team_leader_invite();

-- Verifica che l'invitato sia registrato all'hackathon
CREATE
OR REPLACE FUNCTION check_invitee_registered()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = NEW.team_id;

IF
NOT EXISTS (
        SELECT 1
        FROM registrazioni
        WHERE utente_id = NEW.invitato_id
        AND hackathon_id = hack_id
    ) THEN
        RAISE EXCEPTION 'L''utente invitato deve essere registrato all''hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_invitee_registered
    BEFORE INSERT
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION check_invitee_registered();

-- Aggiorna data_risposta quando si accetta/rifiuta un invito
CREATE
OR REPLACE FUNCTION update_invite_response_date()
RETURNS TRIGGER AS $$
BEGIN
    IF
OLD.stato_invito_id = 1 AND NEW.stato_invito_id IN (2, 3) THEN
        NEW.data_risposta = CURRENT_TIMESTAMP;
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_invite_response_date
    BEFORE UPDATE
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION update_invite_response_date();

-- Automaticamente unisci al team quando l'invito viene accettato
CREATE
OR REPLACE FUNCTION auto_join_team_on_accept()
RETURNS TRIGGER AS $$
BEGIN
    IF
NEW.stato_invito_id = 2 AND OLD.stato_invito_id = 1 THEN
        INSERT INTO membri_team (team_id, utente_id, ruolo_team)
        VALUES (NEW.team_id, NEW.invitato_id, 'MEMBRO')
        ON CONFLICT DO NOTHING;
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_auto_join_team
    AFTER UPDATE
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION auto_join_team_on_accept();

-- Verifica che ci sia almeno un leader per team
CREATE
OR REPLACE FUNCTION ensure_team_has_leader()
RETURNS TRIGGER AS $$
DECLARE
leader_count INTEGER;
BEGIN
    IF
OLD.ruolo_team = 'LEADER' THEN
SELECT COUNT(*)
INTO leader_count
FROM membri_team
WHERE team_id = OLD.team_id
  AND ruolo_team = 'LEADER'
  AND utente_id != OLD.utente_id;

IF
leader_count = 0 THEN
            RAISE EXCEPTION 'Il team deve avere almeno un leader';
END IF;
END IF;
RETURN OLD;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_ensure_team_leader_on_delete
    BEFORE DELETE
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION ensure_team_has_leader();

CREATE TRIGGER trigger_ensure_team_leader_on_update
    BEFORE UPDATE OF ruolo_team
    ON membri_team
    FOR EACH ROW WHEN (OLD.ruolo_team = 'LEADER' AND NEW.ruolo_team != 'LEADER')
    EXECUTE FUNCTION ensure_team_has_leader();

-- ==================================================
-- SEZIONE 6: VIEWS
-- ==================================================

CREATE VIEW classifica_hackathon AS
SELECT h.hackathon_id,
       h.titolo            AS hackathon_titolo,
       t.team_id,
       t.nome              AS team_nome,
       AVG(v.valore)       AS media_voti,
       COUNT(v.voto_id)    AS numero_voti,
       COUNT(mt.utente_id) AS numero_membri,
       h.max_dimensione_team
FROM hackathon h
         JOIN team t ON t.hackathon_id = h.hackathon_id
         LEFT JOIN voti v ON v.team_id = t.team_id
         LEFT JOIN membri_team mt ON mt.team_id = t.team_id
WHERE t.definitivo = TRUE
GROUP BY h.hackathon_id, h.titolo, t.team_id, t.nome, h.max_dimensione_team
ORDER BY h.hackathon_id, AVG(v.valore) DESC, t.nome;