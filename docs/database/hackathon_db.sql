-- ==================================================
-- TABELLE ENUM
-- ==================================================

-- Ruoli utente nel sistema
CREATE TABLE user_roles
(
    role_id   INTEGER PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- Stati possibili di un hackathon
CREATE TABLE hackathon_status
(
    status_id   INTEGER PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

-- Stati possibili per gli inviti
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
-- ENTITÀ PRINCIPALI
-- ==================================================

-- Utenti registrati al sistema
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

    -- Vincoli di validazione input
    CONSTRAINT check_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
) ,
    CONSTRAINT check_username_length
        CHECK (LENGTH(username) >= 3),
    CONSTRAINT check_password_length
        CHECK (LENGTH(password) >= 6)
);

-- Eventi hackathon organizzati
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
    max_dimensione_team         INTEGER      NOT NULL CHECK (max_dimensione_team >= 2),
    organizzatore_id            INTEGER      NOT NULL REFERENCES utenti (utente_id),
    status_id                   INTEGER      NOT NULL REFERENCES hackathon_status (status_id) DEFAULT 1,
    data_creazione              TIMESTAMP                                                     DEFAULT CURRENT_TIMESTAMP,

    -- Vincoli temporali e logici
    CONSTRAINT check_date_order CHECK (data_inizio < data_fine),
    CONSTRAINT check_registration_deadline CHECK (data_chiusura_registrazioni <= data_inizio)
);

-- Team partecipanti agli hackathon
CREATE TABLE team
(
    team_id        SERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    hackathon_id   INTEGER      NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    definitivo     BOOLEAN   DEFAULT FALSE,

    -- Nome team univoco per hackathon
    CONSTRAINT unique_team_name_per_hackathon UNIQUE (nome, hackathon_id)
);

-- ==================================================
-- RELAZIONI DIRETTE
-- ==================================================

-- Iscrizioni degli utenti agli hackathon
CREATE TABLE registrazioni
(
    registrazione_id   SERIAL PRIMARY KEY,
    utente_id          INTEGER NOT NULL REFERENCES utenti (utente_id),
    hackathon_id       INTEGER NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    data_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    team_id            INTEGER REFERENCES team (team_id) ON DELETE SET NULL,

    -- Un utente può registrarsi una sola volta per hackathon
    CONSTRAINT unique_registration_per_user_hackathon UNIQUE (utente_id, hackathon_id)
);

-- Assegnazione giudici agli hackathon
CREATE TABLE giudici_hackathon
(
    giudice_hackathon_id SERIAL PRIMARY KEY,
    hackathon_id         INTEGER NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    giudice_id           INTEGER NOT NULL REFERENCES utenti (utente_id),

    -- Un giudice può essere assegnato una sola volta per hackathon
    CONSTRAINT unique_judge_per_hackathon UNIQUE (hackathon_id, giudice_id)
);

-- Composizione dei team
CREATE TABLE membri_team
(
    membro_team_id SERIAL PRIMARY KEY,
    team_id        INTEGER NOT NULL REFERENCES team (team_id) ON DELETE CASCADE,
    utente_id      INTEGER NOT NULL REFERENCES utenti (utente_id),
    data_ingresso  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    ruolo_team     VARCHAR(20) DEFAULT 'MEMBRO' CHECK (ruolo_team IN ('LEADER', 'MEMBRO')),

    -- Un utente può essere in un solo team alla volta
    CONSTRAINT unique_user_per_team UNIQUE (team_id, utente_id)
);

-- ==================================================
-- CONTENUTI E VALUTAZIONI
-- ==================================================

-- Problemi pubblicati dai giudici
CREATE TABLE problema
(
    problema_id        SERIAL PRIMARY KEY,
    pubblicato_da      INTEGER      NOT NULL REFERENCES giudici_hackathon (giudice_hackathon_id) ON DELETE CASCADE,
    titolo             VARCHAR(200) NOT NULL,
    descrizione        TEXT         NOT NULL,
    data_pubblicazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Documenti di progresso caricati dai team
CREATE TABLE progressi
(
    progresso_id     SERIAL PRIMARY KEY,
    caricato_da      INTEGER NOT NULL REFERENCES membri_team (membro_team_id) ON DELETE CASCADE,
    documento_path   VARCHAR(500),
    documento_nome   VARCHAR(200),
    data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feedback dei giudici sui progressi
CREATE TABLE commenti
(
    commento_id          SERIAL PRIMARY KEY,
    progresso_id         INTEGER NOT NULL REFERENCES progressi (progresso_id) ON DELETE CASCADE,
    giudice_hackathon_id INTEGER NOT NULL REFERENCES giudici_hackathon (giudice_hackathon_id),
    testo                TEXT    NOT NULL,
    data_commento        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Valutazioni finali dei team
CREATE TABLE voti
(
    voto_id              SERIAL PRIMARY KEY,
    team_id              INTEGER NOT NULL REFERENCES team (team_id) ON DELETE CASCADE,
    giudice_hackathon_id INTEGER NOT NULL REFERENCES giudici_hackathon (giudice_hackathon_id),
    valore               INTEGER NOT NULL CHECK (valore >= 1 AND valore <= 10),
    data_voto            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Un giudice può votare un team una sola volta
    CONSTRAINT unique_vote_per_judge_team UNIQUE (team_id, giudice_hackathon_id)
);

-- ==================================================
-- SISTEMA INVITI
-- ==================================================

-- Inviti per unirsi ai team
CREATE TABLE inviti_team
(
    invito_id               SERIAL PRIMARY KEY,
    invitante_id            INTEGER NOT NULL REFERENCES membri_team (membro_team_id) ON DELETE CASCADE,
    invitato_id             INTEGER NOT NULL REFERENCES utenti (utente_id) ON DELETE CASCADE,
    messaggio_motivazionale TEXT,
    stato_invito_id         INTEGER   DEFAULT 1 REFERENCES invito_status (status_id),
    data_invito             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Vincoli di integrità
    CONSTRAINT unique_team_invite UNIQUE (invitante_id, invitato_id),
    CONSTRAINT check_no_self_invite_team CHECK (invitante_id != invitato_id
) );

-- Inviti per giudicare hackathon
CREATE TABLE inviti_giudice
(
    invito_id       SERIAL PRIMARY KEY,
    invitante_id    INTEGER NOT NULL REFERENCES utenti (utente_id) ON DELETE CASCADE,
    invitato_id     INTEGER NOT NULL REFERENCES utenti (utente_id) ON DELETE CASCADE,
    hackathon_id    INTEGER NOT NULL REFERENCES hackathon (hackathon_id) ON DELETE CASCADE,
    stato_invito_id INTEGER   DEFAULT 1 REFERENCES invito_status (status_id),
    data_invito     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Vincoli di integrità
    CONSTRAINT unique_judge_invite UNIQUE (invitante_id, invitato_id, hackathon_id),
    CONSTRAINT check_no_self_invite_judge CHECK (invitante_id != invitato_id
) );

-- ==================================================
-- TRIGGER PER CONTROLLO RUOLI E PERMESSI
-- ==================================================

-- TR01: Solo organizzatori possono creare hackathon
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

CREATE TRIGGER tr01_check_organizzatore_role
    BEFORE INSERT OR
UPDATE ON hackathon FOR EACH ROW EXECUTE FUNCTION check_organizzatore_role();

-- TR02: Solo utenti con ruolo GIUDICE possono essere aggiunti come giudici
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
        RAISE EXCEPTION 'Solo gli utenti con ruolo GIUDICE possono essere aggiunti come giudici';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr02_check_giudice_role
    BEFORE INSERT OR
UPDATE ON giudici_hackathon FOR EACH ROW EXECUTE FUNCTION check_giudice_role();

-- TR03: Solo membri del team possono caricare progressi
CREATE
OR REPLACE FUNCTION check_team_member_progress()
RETURNS TRIGGER AS $$
BEGIN
    -- Verifica che il membro_team_id esista
    IF
NOT EXISTS (
        SELECT 1 FROM membri_team
        WHERE membro_team_id = NEW.caricato_da
    ) THEN
        RAISE EXCEPTION 'Il membro del team specificato non esiste';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr03_team_member_progress
    BEFORE INSERT
    ON progressi
    FOR EACH ROW EXECUTE FUNCTION check_team_member_progress();

-- TR04: Solo giudici dell'hackathon possono commentare
CREATE
OR REPLACE FUNCTION check_judge_can_comment()
RETURNS TRIGGER AS $$
BEGIN
    -- Verifica che il giudice_hackathon_id esista
    IF
NOT EXISTS (
        SELECT 1 FROM giudici_hackathon
        WHERE giudice_hackathon_id = NEW.giudice_hackathon_id
    ) THEN
        RAISE EXCEPTION 'Solo i giudici assegnati all''hackathon possono commentare i progressi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr04_judge_can_comment
    BEFORE INSERT
    ON commenti
    FOR EACH ROW EXECUTE FUNCTION check_judge_can_comment();

-- TR05: Solo giudici dell'hackathon possono votare
CREATE
OR REPLACE FUNCTION check_judge_can_vote()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal team
SELECT h.hackathon_id
INTO hack_id
FROM team t
         JOIN hackathon h ON h.hackathon_id = t.hackathon_id
WHERE t.team_id = NEW.team_id;

-- Verifica che il giudice sia assegnato a questo hackathon
IF
NOT EXISTS (
        SELECT 1 FROM giudici_hackathon
        WHERE giudice_hackathon_id = NEW.giudice_hackathon_id
        AND hackathon_id = hack_id
    ) THEN
        RAISE EXCEPTION 'Solo i giudici assegnati a questo hackathon possono votare';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr05_judge_can_vote
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_judge_can_vote();

-- TR06: Solo il leader del team può invitare membri
CREATE
OR REPLACE FUNCTION check_team_leader_invite()
RETURNS TRIGGER AS $$
BEGIN
    -- Verifica che l'invitante sia il leader del team
    IF
NOT EXISTS (
        SELECT 1 FROM membri_team
        WHERE membro_team_id = NEW.invitante_id
        AND ruolo_team = 'LEADER'
    ) THEN
        RAISE EXCEPTION 'Solo il leader del team può invitare nuovi membri';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr06_team_leader_invite
    BEFORE INSERT
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION check_team_leader_invite();

-- ==================================================
-- TRIGGER PER GESTIONE TEAM E MEMBRI
-- ==================================================

-- TR07: Un utente può essere in un solo team per hackathon
CREATE
OR REPLACE FUNCTION check_single_team_per_hackathon()
RETURNS TRIGGER AS $$
BEGIN
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

CREATE TRIGGER tr07_single_team_per_hackathon
    BEFORE INSERT OR
UPDATE ON membri_team FOR EACH ROW EXECUTE FUNCTION check_single_team_per_hackathon();

-- TR08: Verifica dimensione massima team
CREATE
OR REPLACE FUNCTION check_team_max_size()
RETURNS TRIGGER AS $$
DECLARE
current_size INTEGER;
    max_size
INTEGER;
BEGIN
    -- Conta i membri attuali del team
SELECT COUNT(*)
INTO current_size
FROM membri_team
WHERE team_id = NEW.team_id;

-- Ottieni la dimensione massima dall'hackathon
SELECT h.max_dimensione_team
INTO max_size
FROM team t
         JOIN hackathon h ON h.hackathon_id = t.hackathon_id
WHERE t.team_id = NEW.team_id;

IF
current_size >= max_size THEN
        RAISE EXCEPTION 'Il team ha raggiunto la dimensione massima di % membri', max_size;
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr08_team_max_size
    BEFORE INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION check_team_max_size();

-- TR09: Solo un leader per team
CREATE
OR REPLACE FUNCTION check_single_leader_per_team()
RETURNS TRIGGER AS $$
BEGIN
    IF
NEW.ruolo_team = 'LEADER' AND EXISTS (
        SELECT 1 FROM membri_team
        WHERE team_id = NEW.team_id
        AND ruolo_team = 'LEADER'
        AND membro_team_id != COALESCE(NEW.membro_team_id, -1)
    ) THEN
        RAISE EXCEPTION 'Un team può avere un solo leader';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr09_single_leader_per_team
    BEFORE INSERT OR
UPDATE ON membri_team FOR EACH ROW EXECUTE FUNCTION check_single_leader_per_team();

-- TR10: Un utente deve essere registrato all'hackathon per unirsi al team
CREATE
OR REPLACE FUNCTION check_user_registered_for_team()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal team
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = NEW.team_id;

-- Verifica che l'utente sia registrato all'hackathon
IF
NOT EXISTS (
        SELECT 1 FROM registrazioni
        WHERE utente_id = NEW.utente_id
        AND hackathon_id = hack_id
    ) THEN
        RAISE EXCEPTION 'L''utente deve essere registrato all''hackathon per unirsi al team';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr10_user_registered_for_team
    BEFORE INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION check_user_registered_for_team();

-- TR11: Si possono votare solo team definitivi
CREATE
OR REPLACE FUNCTION check_team_definitivo_for_vote()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM team
        WHERE team_id = NEW.team_id
        AND definitivo = TRUE
    ) THEN
        RAISE EXCEPTION 'Si possono votare solo team definitivi';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr11_team_definitivo_for_vote
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_team_definitivo_for_vote();

-- TR12: L'invitato deve essere registrato all'hackathon
CREATE
OR REPLACE FUNCTION check_invitee_registered()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
    team_id_val
INTEGER;
BEGIN
    -- Ottieni team_id dall'invitante
SELECT team_id
INTO team_id_val
FROM membri_team
WHERE membro_team_id = NEW.invitante_id;

-- Ottieni hackathon_id dal team
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = team_id_val;

-- Verifica che l'invitato sia registrato all'hackathon
IF
NOT EXISTS (
        SELECT 1 FROM registrazioni
        WHERE utente_id = NEW.invitato_id
        AND hackathon_id = hack_id
    ) THEN
        RAISE EXCEPTION 'L''utente invitato deve essere registrato all''hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr12_invitee_registered
    BEFORE INSERT
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION check_invitee_registered();

-- ==================================================
-- TRIGGER PER CONTROLLO STATO HACKATHON
-- ==================================================

-- TR13: No registrazioni dopo la deadline
CREATE
OR REPLACE FUNCTION check_registration_deadline()
RETURNS TRIGGER AS $$
BEGIN
    IF
EXISTS (
        SELECT 1 FROM hackathon
        WHERE hackathon_id = NEW.hackathon_id
        AND data_chiusura_registrazioni < CURRENT_TIMESTAMP
    ) THEN
        RAISE EXCEPTION 'Le registrazioni per questo hackathon sono chiuse';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr13_registration_deadline
    BEFORE INSERT
    ON registrazioni
    FOR EACH ROW EXECUTE FUNCTION check_registration_deadline();

-- TR14: Verifica limite massimo partecipanti
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

CREATE TRIGGER tr14_max_participants
    BEFORE INSERT
    ON registrazioni
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_max_participants();

-- TR15: Si può votare solo durante o dopo l'hackathon
CREATE
OR REPLACE FUNCTION check_hackathon_status_for_vote()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal team
SELECT h.hackathon_id
INTO hack_id
FROM team t
         JOIN hackathon h ON h.hackathon_id = t.hackathon_id
WHERE t.team_id = NEW.team_id;

-- Verifica che l'hackathon sia in corso o terminato
IF
NOT EXISTS (
        SELECT 1 FROM hackathon
        WHERE hackathon_id = hack_id
        AND status_id IN (3, 4)  -- IN_CORSO o TERMINATO
    ) THEN
        RAISE EXCEPTION 'Si può votare solo durante o dopo l''hackathon';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr15_hackathon_status_for_vote
    BEFORE INSERT
    ON voti
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_status_for_vote();

-- TR16: Problemi solo quando hackathon in corso
CREATE
OR REPLACE FUNCTION check_hackathon_in_corso_for_problema()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Per INSERT e UPDATE, controlla NEW
    IF
TG_OP IN ('INSERT', 'UPDATE') THEN
        -- Ottieni hackathon_id dal giudice
SELECT hackathon_id
INTO hack_id
FROM giudici_hackathon
WHERE giudice_hackathon_id = NEW.pubblicato_da;

IF
NOT EXISTS (
            SELECT 1 FROM hackathon h
            WHERE h.hackathon_id = hack_id
            AND h.status_id = 3  -- IN_CORSO
        ) THEN
            RAISE EXCEPTION 'I problemi possono essere gestiti solo quando l''hackathon è in corso';
END IF;
RETURN NEW;
END IF;

    -- Per DELETE, controlla OLD
    IF
TG_OP = 'DELETE' THEN
        -- Ottieni hackathon_id dal giudice
SELECT hackathon_id
INTO hack_id
FROM giudici_hackathon
WHERE giudice_hackathon_id = OLD.pubblicato_da;

IF
NOT EXISTS (
            SELECT 1 FROM hackathon h
            WHERE h.hackathon_id = hack_id
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

CREATE TRIGGER tr16a_hackathon_in_corso_for_problema_insert
    BEFORE INSERT
    ON problema
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_in_corso_for_problema();

CREATE TRIGGER tr16b_hackathon_in_corso_for_problema_update
    BEFORE UPDATE
    ON problema
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_in_corso_for_problema();

CREATE TRIGGER tr16c_hackathon_in_corso_for_problema_delete
    BEFORE DELETE
    ON problema
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_in_corso_for_problema();

-- TR17: No progressi se hackathon terminato
CREATE
OR REPLACE FUNCTION check_hackathon_not_terminated_for_progress()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
    team_id_val
INTEGER;
BEGIN
    -- Determina quale record controllare
    IF
TG_OP IN ('INSERT', 'UPDATE') THEN
        -- Ottieni team_id dal membro
SELECT team_id
INTO team_id_val
FROM membri_team
WHERE membro_team_id = NEW.caricato_da;
ELSE
        -- Per DELETE
SELECT team_id
INTO team_id_val
FROM membri_team
WHERE membro_team_id = OLD.caricato_da;
END IF;

    -- Ottieni hackathon_id dal team
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = team_id_val;

-- Verifica che l'hackathon non sia terminato
IF
EXISTS (
        SELECT 1 FROM hackathon
        WHERE hackathon_id = hack_id
        AND status_id = 4  -- TERMINATO
    ) THEN
        IF TG_OP = 'DELETE' THEN
            RAISE EXCEPTION 'Non è possibile eliminare progressi per un hackathon terminato';
ELSE
            RAISE EXCEPTION 'Non è possibile caricare progressi per un hackathon terminato';
END IF;
END IF;

    IF
TG_OP IN ('INSERT', 'UPDATE') THEN
        RETURN NEW;
ELSE
        RETURN OLD;
END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr17a_hackathon_not_terminated_for_progress_insert
    BEFORE INSERT OR
UPDATE ON progressi FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_progress();

CREATE TRIGGER tr17b_hackathon_not_terminated_for_progress_delete
    BEFORE DELETE
    ON progressi
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_progress();

-- TR18: No modifiche team se hackathon terminato
CREATE
OR REPLACE FUNCTION check_hackathon_not_terminated_for_team()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Per UPDATE del team
    IF
TG_OP = 'UPDATE' THEN
        hack_id := NEW.hackathon_id;
END IF;

    -- Per DELETE del team
    IF
TG_OP = 'DELETE' THEN
        hack_id := OLD.hackathon_id;
END IF;

    -- Verifica che l'hackathon non sia terminato
    IF
EXISTS (
        SELECT 1 FROM hackathon
        WHERE hackathon_id = hack_id
        AND status_id = 4  -- TERMINATO
    ) THEN
        RAISE EXCEPTION 'Non è possibile modificare team per un hackathon terminato';
END IF;

    IF
TG_OP = 'UPDATE' THEN
        RETURN NEW;
ELSE
        RETURN OLD;
END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr18a_hackathon_not_terminated_for_team_update
    BEFORE UPDATE
    ON team
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_team();

CREATE TRIGGER tr18b_hackathon_not_terminated_for_team_delete
    BEFORE DELETE
    ON team
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_team();

-- TR19: No modifiche membri team se hackathon terminato
CREATE
OR REPLACE FUNCTION check_hackathon_not_terminated_for_team_members()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
    team_id_check
INTEGER;
BEGIN
    -- Determina il team_id da controllare
    IF
TG_OP IN ('INSERT', 'UPDATE') THEN
        team_id_check := NEW.team_id;
ELSE
        team_id_check := OLD.team_id;
END IF;

    -- Ottieni l'hackathon_id dal team
SELECT h.hackathon_id
INTO hack_id
FROM team t
         JOIN hackathon h ON h.hackathon_id = t.hackathon_id
WHERE t.team_id = team_id_check;

-- Verifica che l'hackathon non sia terminato
IF
EXISTS (
        SELECT 1 FROM hackathon
        WHERE hackathon_id = hack_id
        AND status_id = 4  -- TERMINATO
    ) THEN
        RAISE EXCEPTION 'Non è possibile modificare i membri del team per un hackathon terminato';
END IF;

    IF
TG_OP IN ('INSERT', 'UPDATE') THEN
        RETURN NEW;
ELSE
        RETURN OLD;
END IF;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr19a_hackathon_not_terminated_for_team_members_insert
    BEFORE INSERT OR
UPDATE ON membri_team FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_team_members();

CREATE TRIGGER tr19b_hackathon_not_terminated_for_team_members_delete
    BEFORE DELETE
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION check_hackathon_not_terminated_for_team_members();

-- ==================================================
-- TRIGGER PER AUTOMAZIONE E SINCRONIZZAZIONE
-- ==================================================

-- TR20: Aggiorna team_id in registrazioni quando utente si unisce a team
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

CREATE TRIGGER tr20_update_registration_team
    AFTER INSERT
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION update_registration_team();

-- TR21: Auto-join team quando si accetta invito
CREATE
OR REPLACE FUNCTION auto_join_team_on_accept()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
    team_size
INTEGER;
    max_size
INTEGER;
    team_id_val
INTEGER;
BEGIN
    -- Solo se l'invito passa da PENDING ad ACCEPTED
    IF
NEW.stato_invito_id = 2 AND OLD.stato_invito_id = 1 THEN

        -- Ottieni team_id dall'invitante
SELECT team_id
INTO team_id_val
FROM membri_team
WHERE membro_team_id = NEW.invitante_id;

-- Ottieni l'hackathon_id e verifica la dimensione del team
SELECT t.hackathon_id, h.max_dimensione_team, COUNT(mt.utente_id)
INTO hack_id, max_size, team_size
FROM team t
         JOIN hackathon h ON h.hackathon_id = t.hackathon_id
         LEFT JOIN membri_team mt ON mt.team_id = t.team_id
WHERE t.team_id = team_id_val
GROUP BY t.hackathon_id, h.max_dimensione_team;

-- Verifica che il team non sia pieno
IF
team_size >= max_size THEN
            RAISE EXCEPTION 'Impossibile accettare l''invito: il team ha raggiunto la dimensione massima';
END IF;

        -- Verifica che l'utente non sia già in un altro team per questo hackathon
        IF
EXISTS (
            SELECT 1
            FROM membri_team mt
            JOIN team t ON mt.team_id = t.team_id
            WHERE mt.utente_id = NEW.invitato_id
            AND t.hackathon_id = hack_id
        ) THEN
            RAISE EXCEPTION 'L''utente è già membro di un team per questo hackathon';
END IF;

        -- Aggiungi l'utente al team
INSERT INTO membri_team (team_id, utente_id, ruolo_team)
VALUES (team_id_val, NEW.invitato_id, 'MEMBRO');

END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr21_auto_join_team_on_accept
    AFTER UPDATE
    ON inviti_team
    FOR EACH ROW EXECUTE FUNCTION auto_join_team_on_accept();

-- ==================================================
-- TRIGGER PER INVITI GIUDICE (NUOVI)
-- ==================================================

-- TR22: Solo organizzatori possono invitare giudici
CREATE
OR REPLACE FUNCTION check_organizzatore_can_invite_judge()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti
        WHERE utente_id = NEW.invitante_id
        AND tipo_utente_id = 1  -- ORGANIZZATORE
    ) THEN
        RAISE EXCEPTION 'Solo gli organizzatori possono invitare giudici';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr22_organizzatore_can_invite_judge
    BEFORE INSERT
    ON inviti_giudice
    FOR EACH ROW EXECUTE FUNCTION check_organizzatore_can_invite_judge();

-- TR23: Solo utenti con ruolo GIUDICE possono essere invitati come giudici
CREATE
OR REPLACE FUNCTION check_invited_user_is_judge()
RETURNS TRIGGER AS $$
BEGIN
    IF
NOT EXISTS (
        SELECT 1 FROM utenti
        WHERE utente_id = NEW.invitato_id
        AND tipo_utente_id = 2  -- GIUDICE
    ) THEN
        RAISE EXCEPTION 'Solo gli utenti con ruolo GIUDICE possono essere invitati come giudici';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr23_invited_user_is_judge
    BEFORE INSERT
    ON inviti_giudice
    FOR EACH ROW EXECUTE FUNCTION check_invited_user_is_judge();

-- ==================================================
-- TRIGGER PER CONTROLLI AGGIUNTIVI
-- ==================================================

-- TR24: Rimuovi team_id da registrazioni quando utente lascia team
CREATE
OR REPLACE FUNCTION remove_team_from_registration()
RETURNS TRIGGER AS $$
DECLARE
hack_id INTEGER;
BEGIN
    -- Ottieni l'hackathon_id dal team
SELECT hackathon_id
INTO hack_id
FROM team
WHERE team_id = OLD.team_id;

-- Rimuovi team_id dalla registrazione
UPDATE registrazioni
SET team_id = NULL
WHERE utente_id = OLD.utente_id
  AND hackathon_id = hack_id;

RETURN OLD;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tr24_remove_team_from_registration
    AFTER DELETE
    ON membri_team
    FOR EACH ROW EXECUTE FUNCTION remove_team_from_registration();

-- ==================================================
-- VIEWS
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