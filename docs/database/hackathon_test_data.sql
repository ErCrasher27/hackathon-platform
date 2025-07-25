-- DISABILITA TRIGGER
ALTER TABLE utenti DISABLE TRIGGER ALL;
ALTER TABLE hackathons DISABLE TRIGGER ALL;
ALTER TABLE teams DISABLE TRIGGER ALL;
ALTER TABLE registrazioni DISABLE TRIGGER ALL;
ALTER TABLE giudici_hackathon DISABLE TRIGGER ALL;
ALTER TABLE problemi DISABLE TRIGGER ALL;
ALTER TABLE progressi DISABLE TRIGGER ALL;
ALTER TABLE commenti DISABLE TRIGGER ALL;
ALTER TABLE voti DISABLE TRIGGER ALL;
ALTER TABLE inviti_team DISABLE TRIGGER ALL;
ALTER TABLE inviti_giudice DISABLE TRIGGER ALL;

-- UTENTI
INSERT INTO utenti (utente_id, username, email, password, nome, cognome, ruolo_fk_ruoli_utente)
VALUES (1, 'admin1', 'admin1@example.com', 'password123', 'Alice', 'Admin', 1),
       (2, 'judge1', 'judge1@example.com', 'password123', 'Bob', 'Judge', 2),
       (3, 'judge2', 'judge2@example.com', 'password123', 'Charlie', 'Judge', 2),
       (4, 'user1', 'user1@example.com', 'password123', 'Diana', 'User', 3),
       (5, 'user2', 'user2@example.com', 'password123', 'Eve', 'User', 3),
       (6, 'user3', 'user3@example.com', 'password123', 'Frank', 'User', 3),
       (7, 'user4', 'user4@example.com', 'password123', 'Grace', 'User', 3),
       (8, 'user5', 'user5@example.com', 'password123', 'Heidi', 'User', 3);

-- HACKATHONS
INSERT INTO hackathons (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_reg,
                        max_iscritti, max_membri_team, organizzatore_fk_utenti, stato_fk_stati_hackathon)
VALUES (1, 'Hackathon Aperto', 'Evento in corso', 'Napoli', NOW() + INTERVAL '2 days', NOW() + INTERVAL '4 days',
        NOW() + INTERVAL '1 days', 100, 3, 1, 1),
       (2, 'Hackathon Chiuso', 'Registrazioni chiuse', 'Milano', NOW() + INTERVAL '5 days', NOW() + INTERVAL '6 days',
        NOW() - INTERVAL '1 days', 50, 2, 1, 2),
       (3, 'Hackathon Terminato', 'Evento completato', 'Roma', NOW() - INTERVAL '10 days', NOW() - INTERVAL '8 days',
        NOW() - INTERVAL '12 days', 80, 4, 1, 4);

-- TEAM
INSERT INTO teams (team_id, nome, hackathon_fk_hackathons, definitivo)
VALUES (1, 'Team Alpha', 1, TRUE),
       (2, 'Team Beta', 1, TRUE),
       (3, 'Team Gamma', 2, FALSE),
       (4, 'Team Omega', 3, TRUE);

-- REGISTRAZIONI
INSERT INTO registrazioni (registrazione_id, partecipante_fk_utenti, hackathon_fk_hackathons, team_fk_teams,
                           ruolo_fk_ruoli_team, data_ingresso_team)
VALUES (1, 4, 1, 1, 1, NOW()),
       (2, 5, 1, 1, 2, NOW()),
       (3, 6, 1, 2, 1, NOW()),
       (4, 7, 1, 2, 2, NOW()),
       (5, 4, 2, 3, 1, NOW()),
       (6, 8, 3, 4, 1, NOW());

-- GIUDICI HACKATHON
INSERT INTO giudici_hackathon (giudice_hackathon_id, hackathon_fk_hackathons, giudice_fk_utenti)
VALUES (1, 1, 2),
       (2, 3, 3);

-- PROBLEMI
INSERT INTO problemi (problema_id, giudice_hack_fk_giudici_hackathon, titolo, descrizione)
VALUES (1, 1, 'Problema Frontend', 'Descrizione problema UI'),
       (2, 2, 'Problema Backend', 'Descrizione problema server');

-- PROGRESSI
INSERT INTO progressi (progresso_id, registrazione_fk_registrazioni, documento_path, documento_nome)
VALUES (1, 1, '/docs/progress1.pdf', 'Progress1'),
       (2, 2, '/docs/progress2.pdf', 'Progress2');

-- COMMENTI
INSERT INTO commenti (commento_id, progresso_fk_progressi, giudice_hack_fk_giudici_hackathon, testo)
VALUES (1, 1, 1, 'Ottimo lavoro!'),
       (2, 2, 1, 'Serve più dettaglio.');

-- VOTI
INSERT INTO voti (voto_id, team_fk_teams, giudice_hack_fk_giudici_hackathon, valore)
VALUES (1, 1, 1, 9),
       (2, 2, 1, 8),
       (3, 4, 2, 7);

-- INVITI TEAM
INSERT INTO inviti_team (invito_id, invitante_reg_fk_registrazioni, invitato_fk_utenti, messaggio,
                         stato_fk_stati_invito)
VALUES (1, 1, 8, 'Unisciti a noi!', 2);

-- INVITI GIUDICE
INSERT INTO inviti_giudice (invito_id, invitante_fk_utenti, invitato_fk_utenti, hackathon_fk_hackathons,
                            stato_fk_stati_invito)
VALUES (1, 1, 3, 1, 2);

-- REIMPOSTA LE SEQUENZE SERIAL
SELECT setval('utenti_utente_id_seq', (SELECT MAX(utente_id) FROM utenti));
SELECT setval('hackathons_hackathon_id_seq', (SELECT MAX(hackathon_id) FROM hackathons));
SELECT setval('teams_team_id_seq', (SELECT MAX(team_id) FROM teams));
SELECT setval('registrazioni_registrazione_id_seq', (SELECT MAX(registrazione_id) FROM registrazioni));
SELECT setval('giudici_hackathon_giudice_hackathon_id_seq', (SELECT MAX(giudice_hackathon_id) FROM giudici_hackathon));
SELECT setval('problemi_problema_id_seq', (SELECT MAX(problema_id) FROM problemi));
SELECT setval('progressi_progresso_id_seq', (SELECT MAX(progresso_id) FROM progressi));
SELECT setval('commenti_commento_id_seq', (SELECT MAX(commento_id) FROM commenti));
SELECT setval('voti_voto_id_seq', (SELECT MAX(voto_id) FROM voti));
SELECT setval('inviti_team_invito_id_seq', (SELECT MAX(invito_id) FROM inviti_team));
SELECT setval('inviti_giudice_invito_id_seq', (SELECT MAX(invito_id) FROM inviti_giudice));

-- RIABILITA I TRIGGER
ALTER TABLE utenti ENABLE TRIGGER ALL;
ALTER TABLE hackathons ENABLE TRIGGER ALL;
ALTER TABLE teams ENABLE TRIGGER ALL;
ALTER TABLE registrazioni ENABLE TRIGGER ALL;
ALTER TABLE giudici_hackathon ENABLE TRIGGER ALL;
ALTER TABLE problemi ENABLE TRIGGER ALL;
ALTER TABLE progressi ENABLE TRIGGER ALL;
ALTER TABLE commenti ENABLE TRIGGER ALL;
ALTER TABLE voti ENABLE TRIGGER ALL;
ALTER TABLE inviti_team ENABLE TRIGGER ALL;
ALTER TABLE inviti_giudice ENABLE TRIGGER ALL;
