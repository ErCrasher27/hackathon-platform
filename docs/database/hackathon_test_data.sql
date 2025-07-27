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
VALUES
    -- Organizzatori
    (1, 'mrossi', 'mario.rossi@email.it', 'password123', 'Mario', 'Rossi', 1),
    (2, 'aferrari', 'anna.ferrari@email.it', 'password123', 'Anna', 'Ferrari', 1),

    -- Giudici
    (3, 'pverdi', 'paolo.verdi@email.it', 'password123', 'Paolo', 'Verdi', 2),
    (4, 'lneri', 'laura.neri@email.it', 'password123', 'Laura', 'Neri', 2),
    (5, 'gbianchi', 'giorgio.bianchi@email.it', 'password123', 'Giorgio', 'Bianchi', 2),

    -- Partecipanti
    (6, 'sromano', 'sofia.romano@email.it', 'password123', 'Sofia', 'Romano', 3),
    (7, 'mgreco', 'marco.greco@email.it', 'password123', 'Marco', 'Greco', 3),
    (8, 'erusso', 'elena.russo@email.it', 'password123', 'Elena', 'Russo', 3),
    (9, 'acolombo', 'andrea.colombo@email.it', 'password123', 'Andrea', 'Colombo', 3),
    (10, 'ffontana', 'francesca.fontana@email.it', 'password123', 'Francesca', 'Fontana', 3),
    (11, 'lcosta', 'luca.costa@email.it', 'password123', 'Luca', 'Costa', 3),
    (12, 'gmarino', 'giulia.marino@email.it', 'password123', 'Giulia', 'Marino', 3),
    (13, 'rserra', 'roberto.serra@email.it', 'password123', 'Roberto', 'Serra', 3),
    (14, 'mbarbieri', 'martina.barbieri@email.it', 'password123', 'Martina', 'Barbieri', 3),
    (15, 'dlombardi', 'davide.lombardi@email.it', 'password123', 'Davide', 'Lombardi', 3),
    (16, 'vgiordano', 'valentina.giordano@email.it', 'password123', 'Valentina', 'Giordano', 3),
    (17, 'abruno', 'alessandro.bruno@email.it', 'password123', 'Alessandro', 'Bruno', 3),
    (18, 'cgalli', 'chiara.galli@email.it', 'password123', 'Chiara', 'Galli', 3),
    (19, 'mconti', 'matteo.conti@email.it', 'password123', 'Matteo', 'Conti', 3),
    (20, 'smazza', 'simone.mazza@email.it', 'password123', 'Simone', 'Mazza', 3);

-- HACKATHONS
INSERT INTO hackathons (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_reg,
                        max_iscritti, max_membri_team, organizzatore_fk_utenti, stato_fk_stati_hackathon)
VALUES
    -- Hackathon con registrazioni aperte (futuro)
    (1, 'AI Innovation Challenge 2025',
     'Sviluppa soluzioni innovative utilizzando l''intelligenza artificiale per risolvere problemi reali',
     'Milano - Politecnico', NOW() + INTERVAL '30 days', NOW() + INTERVAL '32 days',
     NOW() + INTERVAL '28 days', 100, 4, 1, 1),

    -- Hackathon con registrazioni chiuse (prossimo)
    (2, 'Green Tech Solutions', 'Crea tecnologie sostenibili per combattere il cambiamento climatico',
     'Torino - Environment Park', NOW() + INTERVAL '5 days', NOW() + INTERVAL '7 days',
     NOW() - INTERVAL '1 days', 50, 3, 1, 2),

    -- Hackathon in corso
    (3, 'Smart City Hackathon', 'Progetta soluzioni per rendere le città più intelligenti e vivibili',
     'Roma - Università La Sapienza', NOW() - INTERVAL '1 days', NOW() + INTERVAL '1 days',
     NOW() - INTERVAL '3 days', 60, 4, 2, 3),

    -- Hackathon terminato
    (4, 'FinTech Revolution', 'Rivoluziona il mondo dei servizi finanziari con soluzioni blockchain e AI',
     'Bologna - Opificio Golinelli', NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days',
     NOW() - INTERVAL '22 days', 80, 3, 2, 4);

-- TEAMS
-- Teams per hackathon con registrazioni aperte (non definitivi)
INSERT INTO teams (team_id, nome, hackathon_fk_hackathons, definitivo)
VALUES (1, 'Neural Networkers', 1, FALSE),
       (2, 'Deep Learning Squad', 1, FALSE);

-- Teams per hackathon con registrazioni chiuse (non ancora definitivi)
INSERT INTO teams (team_id, nome, hackathon_fk_hackathons, definitivo)
VALUES (3, 'EcoTech Warriors', 2, FALSE),
       (4, 'Green Innovators', 2, FALSE);

-- Teams per hackathon in corso (definitivi)
INSERT INTO teams (team_id, nome, hackathon_fk_hackathons, definitivo)
VALUES (5, 'Urban Planners', 3, TRUE),
       (6, 'Smart Mobility', 3, TRUE),
       (7, 'City Analytics', 3, TRUE);

-- Teams per hackathon terminato (definitivi)
INSERT INTO teams (team_id, nome, hackathon_fk_hackathons, definitivo)
VALUES (8, 'Blockchain Bankers', 4, TRUE),
       (9, 'CryptoCredit', 4, TRUE),
       (10, 'FinAI Solutions', 4, TRUE),
       (11, 'Digital Payments Pro', 4, TRUE),
       (12, 'Quantum Finance', 4, TRUE);

-- REGISTRAZIONI
-- Hackathon 1 (registrazioni aperte)
INSERT INTO registrazioni (registrazione_id, partecipante_fk_utenti, hackathon_fk_hackathons, team_fk_teams,
                           ruolo_fk_ruoli_team, data_ingresso_team)
VALUES (1, 6, 1, 1, 1, NOW()), -- Sofia leader Team 1
       (2, 7, 1, 1, 2, NOW()), -- Marco membro Team 1
       (3, 8, 1, 2, 1, NOW()), -- Elena leader Team 2
       (4, 9, 1, NULL, NULL, NULL);
-- Andrea senza team

-- Hackathon 2 (registrazioni chiuse)
INSERT INTO registrazioni (registrazione_id, partecipante_fk_utenti, hackathon_fk_hackathons, team_fk_teams,
                           ruolo_fk_ruoli_team, data_ingresso_team)
VALUES (5, 10, 2, 3, 1, NOW() - INTERVAL '2 days'), -- Francesca leader Team 3
       (6, 11, 2, 3, 2, NOW() - INTERVAL '2 days'), -- Luca membro Team 3
       (7, 12, 2, 4, 1, NOW() - INTERVAL '2 days');
-- Giulia leader Team 4

-- Hackathon 3 (in corso)
INSERT INTO registrazioni (registrazione_id, partecipante_fk_utenti, hackathon_fk_hackathons, team_fk_teams,
                           ruolo_fk_ruoli_team, data_ingresso_team)
VALUES (8, 13, 3, 5, 1, NOW() - INTERVAL '4 days'),  -- Roberto leader Team 5
       (9, 14, 3, 5, 2, NOW() - INTERVAL '4 days'),  -- Martina membro Team 5
       (10, 15, 3, 5, 2, NOW() - INTERVAL '4 days'), -- Davide membro Team 5
       (11, 16, 3, 6, 1, NOW() - INTERVAL '4 days'), -- Valentina leader Team 6
       (12, 17, 3, 6, 2, NOW() - INTERVAL '4 days'), -- Alessandro membro Team 6
       (13, 18, 3, 7, 1, NOW() - INTERVAL '4 days'), -- Chiara leader Team 7
       (14, 19, 3, 7, 2, NOW() - INTERVAL '4 days');
-- Matteo membro Team 7

-- Hackathon 4 (terminato)
INSERT INTO registrazioni (registrazione_id, partecipante_fk_utenti, hackathon_fk_hackathons, team_fk_teams,
                           ruolo_fk_ruoli_team, data_ingresso_team)
VALUES (15, 6, 4, 8, 1, NOW() - INTERVAL '23 days'),   -- Sofia leader Team 8
       (16, 7, 4, 8, 2, NOW() - INTERVAL '23 days'),   -- Marco membro Team 8
       (17, 8, 4, 8, 2, NOW() - INTERVAL '23 days'),   -- Elena membro Team 8
       (18, 9, 4, 9, 1, NOW() - INTERVAL '23 days'),   -- Andrea leader Team 9
       (19, 10, 4, 9, 2, NOW() - INTERVAL '23 days'),  -- Francesca membro Team 9
       (20, 11, 4, 10, 1, NOW() - INTERVAL '23 days'), -- Luca leader Team 10
       (21, 12, 4, 10, 2, NOW() - INTERVAL '23 days'), -- Giulia membro Team 10
       (22, 13, 4, 11, 1, NOW() - INTERVAL '23 days'), -- Roberto leader Team 11
       (23, 14, 4, 11, 2, NOW() - INTERVAL '23 days'), -- Martina membro Team 11
       (24, 15, 4, 11, 2, NOW() - INTERVAL '23 days'), -- Davide membro Team 11
       (25, 16, 4, 12, 1, NOW() - INTERVAL '23 days'), -- Valentina leader Team 12
       (26, 17, 4, 12, 2, NOW() - INTERVAL '23 days'), -- Alessandro membro Team 12
       (27, 18, 4, 12, 2, NOW() - INTERVAL '23 days');
-- Chiara membro Team 12

-- GIUDICI HACKATHON
-- Hackathon 1 - nessun giudice ancora (solo inviti)
-- Hackathon 2 - giudice accettato
INSERT INTO giudici_hackathon (giudice_hackathon_id, hackathon_fk_hackathons, giudice_fk_utenti)
VALUES (1, 2, 3);
-- Paolo Verdi

-- Hackathon 3 - giudici accettati
INSERT INTO giudici_hackathon (giudice_hackathon_id, hackathon_fk_hackathons, giudice_fk_utenti)
VALUES (2, 3, 3), -- Paolo Verdi
       (3, 3, 4);
-- Laura Neri

-- Hackathon 4 - giudici che hanno votato
INSERT INTO giudici_hackathon (giudice_hackathon_id, hackathon_fk_hackathons, giudice_fk_utenti)
VALUES (4, 4, 3), -- Paolo Verdi
       (5, 4, 4), -- Laura Neri
       (6, 4, 5);
-- Giorgio Bianchi

-- PROBLEMI
-- Problema per hackathon 3 (in corso)
INSERT INTO problemi (problema_id, giudice_hack_fk_giudici_hackathon, titolo, descrizione)
VALUES (1, 2, 'Sistema di Mobilità Intelligente',
        'Sviluppare un sistema che ottimizzi il traffico urbano utilizzando dati real-time da sensori IoT e predizioni AI'),
       (2, 3, 'Gestione Rifiuti Smart',
        'Creare una piattaforma per ottimizzare la raccolta differenziata con riconoscimento automatico dei rifiuti');

-- Problemi per hackathon 4 (terminato)
INSERT INTO problemi (problema_id, giudice_hack_fk_giudici_hackathon, titolo, descrizione)
VALUES (3, 4, 'Piattaforma DeFi Accessibile',
        'Progettare una soluzione DeFi user-friendly che renda la finanza decentralizzata accessibile a tutti'),
       (4, 5, 'AI per Credit Scoring',
        'Implementare un sistema di credit scoring basato su AI che sia equo e trasparente');

-- PROGRESSI
-- Progressi per hackathon 3 (in corso)
INSERT INTO progressi (progresso_id, registrazione_fk_registrazioni, documento_path, documento_nome)
VALUES (1, 8, '/docs/urban_planners_v1.pdf', 'Analisi preliminare traffico'),
       (2, 11, '/docs/smart_mobility_v1.pdf', 'Prototipo sistema IoT'),
       (3, 13, '/docs/city_analytics_v1.pdf', 'Dashboard analytics v0.1');

-- Progressi per hackathon 4 (terminato)
INSERT INTO progressi (progresso_id, registrazione_fk_registrazioni, documento_path, documento_nome)
VALUES (4, 15, '/docs/blockchain_bankers_final.pdf', 'Soluzione completa DeFi'),
       (5, 18, '/docs/cryptocredit_final.pdf', 'Smart contracts implementati'),
       (6, 20, '/docs/finai_final.pdf', 'Modello AI credit scoring'),
       (7, 22, '/docs/digital_payments_final.pdf', 'App pagamenti blockchain'),
       (8, 25, '/docs/quantum_finance_final.pdf', 'Algoritmi quantum per trading');

-- COMMENTI
-- Commenti per hackathon 3 (in corso)
INSERT INTO commenti (commento_id, progresso_fk_progressi, giudice_hack_fk_giudici_hackathon, testo)
VALUES (1, 1, 2, 'Ottima analisi iniziale, procedete con l''implementazione'),
       (2, 2, 3, 'Il prototipo è promettente, aggiungete più sensori'),
       (3, 3, 2, 'Dashboard ben strutturata, mancano alcune metriche chiave');

-- Commenti per hackathon 4 (terminato)
INSERT INTO commenti (commento_id, progresso_fk_progressi, giudice_hack_fk_giudici_hackathon, testo)
VALUES (4, 4, 4, 'Soluzione innovativa e ben implementata'),
       (5, 5, 5, 'Smart contracts sicuri e ottimizzati'),
       (6, 6, 6, 'Modello AI accurato e ben documentato');

-- VOTI (solo per hackathon 4 - terminato)
INSERT INTO voti (voto_id, team_fk_teams, giudice_hack_fk_giudici_hackathon, valore)
VALUES
    -- Team 8 - Blockchain Bankers
    (1, 8, 4, 9),   -- Paolo: 9
    (2, 8, 5, 10),  -- Laura: 10
    (3, 8, 6, 9),   -- Giorgio: 9
    -- Team 9 - CryptoCredit
    (4, 9, 4, 8),   -- Paolo: 8
    (5, 9, 5, 7),   -- Laura: 7
    (6, 9, 6, 8),   -- Giorgio: 8
    -- Team 10 - FinAI Solutions
    (7, 10, 4, 10), -- Paolo: 10
    (8, 10, 5, 9),  -- Laura: 9
    (9, 10, 6, 10), -- Giorgio: 10
    -- Team 11 - Digital Payments Pro
    (10, 11, 4, 7), -- Paolo: 7
    (11, 11, 5, 6), -- Laura: 6
    (12, 11, 6, 7), -- Giorgio: 7
    -- Team 12 - Quantum Finance
    (13, 12, 4, 8), -- Paolo: 8
    (14, 12, 5, 9), -- Laura: 9
    (15, 12, 6, 8);
-- Giorgio: 8

-- INVITI TEAM
-- Invito pendente per hackathon 1
INSERT INTO inviti_team (invito_id, invitante_reg_fk_registrazioni, invitato_fk_utenti, messaggio,
                         stato_fk_stati_invito)
VALUES (1, 1, 20, 'Ciao Simone, unisciti al nostro team di AI!', 1);
-- Sofia invita Simone

-- INVITI GIUDICE
-- Inviti per hackathon 1 (ancora pendenti)
INSERT INTO inviti_giudice (invito_id, invitante_fk_utenti, invitato_fk_utenti, hackathon_fk_hackathons,
                            stato_fk_stati_invito)
VALUES (1, 1, 3, 1, 1), -- Mario invita Paolo Verdi
       (2, 1, 4, 1, 1);
-- Mario invita Laura Neri

-- Inviti per altri hackathon (accettati)
INSERT INTO inviti_giudice (invito_id, invitante_fk_utenti, invitato_fk_utenti, hackathon_fk_hackathons,
                            stato_fk_stati_invito)
VALUES (3, 1, 3, 2, 2), -- Paolo ha accettato per hackathon 2
       (4, 2, 3, 3, 2), -- Paolo ha accettato per hackathon 3
       (5, 2, 4, 3, 2), -- Laura ha accettato per hackathon 3
       (6, 2, 3, 4, 2), -- Paolo ha accettato per hackathon 4
       (7, 2, 4, 4, 2), -- Laura ha accettato per hackathon 4
       (8, 2, 5, 4, 2);
-- Giorgio ha accettato per hackathon 4

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