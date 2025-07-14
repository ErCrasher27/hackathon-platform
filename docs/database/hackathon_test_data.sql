-- ==================================================
-- SCRIPT POPOLAMENTO DATABASE HACKATHON
-- ==================================================

-- ==================================================
-- CREAZIONE UTENTI
-- ==================================================

-- Organizzatori (ID: 1-5)
INSERT INTO utenti (utente_id, username, email, password, nome, cognome, tipo_utente_id) VALUES
(1, 'mario.rossi', 'mario.rossi@email.com', 'password123', 'Mario', 'Rossi', 1),
(2, 'giulia.verdi', 'giulia.verdi@email.com', 'password123', 'Giulia', 'Verdi', 1),
(3, 'luigi.bianchi', 'luigi.bianchi@email.com', 'password123', 'Luigi', 'Bianchi', 1),
(4, 'sara.neri', 'sara.neri@email.com', 'password123', 'Sara', 'Neri', 1),
(5, 'paolo.gialli', 'paolo.gialli@email.com', 'password123', 'Paolo', 'Gialli', 1);

-- Giudici (ID: 6-15)
INSERT INTO utenti (utente_id, username, email, password, nome, cognome, tipo_utente_id) VALUES
(6, 'prof.ferrari', 'prof.ferrari@unina.it', 'password123', 'Andrea', 'Ferrari', 2),
(7, 'prof.russo', 'prof.russo@unina.it', 'password123', 'Maria', 'Russo', 2),
(8, 'prof.romano', 'prof.romano@unina.it', 'password123', 'Giuseppe', 'Romano', 2),
(9, 'prof.gallo', 'prof.gallo@unina.it', 'password123', 'Anna', 'Gallo', 2),
(10, 'prof.costa', 'prof.costa@unina.it', 'password123', 'Francesco', 'Costa', 2),
(11, 'dott.fontana', 'dott.fontana@azienda.it', 'password123', 'Laura', 'Fontana', 2),
(12, 'dott.ricci', 'dott.ricci@azienda.it', 'password123', 'Marco', 'Ricci', 2),
(13, 'ing.lombardi', 'ing.lombardi@tech.it', 'password123', 'Silvia', 'Lombardi', 2),
(14, 'ing.moretti', 'ing.moretti@tech.it', 'password123', 'Roberto', 'Moretti', 2),
(15, 'arch.barbieri', 'arch.barbieri@studio.it', 'password123', 'Elena', 'Barbieri', 2);

-- Partecipanti (ID: 16-50)
INSERT INTO utenti (utente_id, username, email, password, nome, cognome, tipo_utente_id) VALUES
(16, 'alessandro.conti', 'alessandro.conti@student.it', 'password123', 'Alessandro', 'Conti', 3),
(17, 'beatrice.leone', 'beatrice.leone@student.it', 'password123', 'Beatrice', 'Leone', 3),
(18, 'carlo.marini', 'carlo.marini@student.it', 'password123', 'Carlo', 'Marini', 3),
(19, 'diana.greco', 'diana.greco@student.it', 'password123', 'Diana', 'Greco', 3),
(20, 'enrico.santoro', 'enrico.santoro@student.it', 'password123', 'Enrico', 'Santoro', 3),
(21, 'federica.rinaldi', 'federica.rinaldi@student.it', 'password123', 'Federica', 'Rinaldi', 3),
(22, 'giorgio.caruso', 'giorgio.caruso@student.it', 'password123', 'Giorgio', 'Caruso', 3),
(23, 'helena.ferrara', 'helena.ferrara@student.it', 'password123', 'Helena', 'Ferrara', 3),
(24, 'ivan.gatti', 'ivan.gatti@student.it', 'password123', 'Ivan', 'Gatti', 3),
(25, 'jessica.mancini', 'jessica.mancini@student.it', 'password123', 'Jessica', 'Mancini', 3),
(26, 'kevin.testa', 'kevin.testa@student.it', 'password123', 'Kevin', 'Testa', 3),
(27, 'lisa.guerra', 'lisa.guerra@student.it', 'password123', 'Lisa', 'Guerra', 3),
(28, 'matteo.serra', 'matteo.serra@student.it', 'password123', 'Matteo', 'Serra', 3),
(29, 'nicole.ferri', 'nicole.ferri@student.it', 'password123', 'Nicole', 'Ferri', 3),
(30, 'oscar.riva', 'oscar.riva@student.it', 'password123', 'Oscar', 'Riva', 3),
(31, 'paola.sala', 'paola.sala@student.it', 'password123', 'Paola', 'Sala', 3),
(32, 'quinn.orlando', 'quinn.orlando@student.it', 'password123', 'Quinn', 'Orlando', 3),
(33, 'rita.piras', 'rita.piras@student.it', 'password123', 'Rita', 'Piras', 3),
(34, 'stefano.longo', 'stefano.longo@student.it', 'password123', 'Stefano', 'Longo', 3),
(35, 'teresa.vitale', 'teresa.vitale@student.it', 'password123', 'Teresa', 'Vitale', 3),
(36, 'umberto.amato', 'umberto.amato@student.it', 'password123', 'Umberto', 'Amato', 3),
(37, 'valeria.pellegrini', 'valeria.pellegrini@student.it', 'password123', 'Valeria', 'Pellegrini', 3),
(38, 'walter.giuliani', 'walter.giuliani@student.it', 'password123', 'Walter', 'Giuliani', 3),
(39, 'xenia.colombo', 'xenia.colombo@student.it', 'password123', 'Xenia', 'Colombo', 3),
(40, 'yuri.messina', 'yuri.messina@student.it', 'password123', 'Yuri', 'Messina', 3),
(41, 'zara.palumbo', 'zara.palumbo@student.it', 'password123', 'Zara', 'Palumbo', 3),
(42, 'alberto.sanna', 'alberto.sanna@student.it', 'password123', 'Alberto', 'Sanna', 3),
(43, 'bianca.farina', 'bianca.farina@student.it', 'password123', 'Bianca', 'Farina', 3),
(44, 'claudio.rizzi', 'claudio.rizzi@student.it', 'password123', 'Claudio', 'Rizzi', 3),
(45, 'daniela.monti', 'daniela.monti@student.it', 'password123', 'Daniela', 'Monti', 3),
(46, 'emanuele.villa', 'emanuele.villa@student.it', 'password123', 'Emanuele', 'Villa', 3),
(47, 'francesca.gentile', 'francesca.gentile@student.it', 'password123', 'Francesca', 'Gentile', 3),
(48, 'giovanni.serra', 'giovanni.serra@student.it', 'password123', 'Giovanni', 'Serra', 3),
(49, 'ilaria.costa', 'ilaria.costa@student.it', 'password123', 'Ilaria', 'Costa', 3),
(50, 'lorenzo.valle', 'lorenzo.valle@student.it', 'password123', 'Lorenzo', 'Valle', 3);

-- ==================================================
-- CREAZIONE HACKATHONS
-- ==================================================

-- Hackathon 1: TERMINATO
INSERT INTO hackathon (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_registrazioni, max_iscritti, max_dimensione_team, organizzatore_id, status_id) VALUES
(1, 'AI Innovation Challenge 2024',
 'Hackathon dedicato all''innovazione nell''intelligenza artificiale. I team dovranno sviluppare soluzioni AI innovative per problemi reali.',
 'Milano - Università Bocconi', 
 '2024-11-15 09:00:00', 
 '2024-11-16 18:00:00', 
 '2024-11-13 23:59:59', 
 50, 
 4, 
 1, 
 4); -- TERMINATO

-- Hackathon 2: IN_CORSO
INSERT INTO hackathon (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_registrazioni, max_iscritti, max_dimensione_team, organizzatore_id, status_id) VALUES
(2, 'Web3 Blockchain Hackathon',
 'Sviluppa la prossima applicazione decentralizzata! Focus su smart contracts, DeFi e NFTs.',
 'Roma - LUISS', 
 CURRENT_DATE + INTERVAL '-1 day' + TIME '09:00:00', 
 CURRENT_DATE + INTERVAL '1 day' + TIME '18:00:00', 
 CURRENT_DATE + INTERVAL '-3 days', 
 30, 
 3, 
 2, 
 3); -- IN_CORSO

-- Hackathon 3: REGISTRAZIONI_CHIUSE
INSERT INTO hackathon (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_registrazioni, max_iscritti, max_dimensione_team, organizzatore_id, status_id) VALUES
(3, 'Green Tech Solutions',
 'Hackathon focalizzato su soluzioni tecnologiche per la sostenibilità ambientale.',
 'Torino - Politecnico', 
 CURRENT_DATE + INTERVAL '2 days' + TIME '09:00:00', 
 CURRENT_DATE + INTERVAL '3 days' + TIME '18:00:00', 
 CURRENT_DATE + INTERVAL '-1 day', 
 40, 
 5, 
 3, 
 2); -- REGISTRAZIONI_CHIUSE

-- Hackathon 4: REGISTRAZIONI_APERTE
INSERT INTO hackathon (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_registrazioni, max_iscritti, max_dimensione_team, organizzatore_id, status_id) VALUES
(4, 'HealthTech Innovation 2025',
 'Rivoluziona il settore sanitario con soluzioni digitali innovative.',
 'Napoli - Federico II', 
 CURRENT_DATE + INTERVAL '30 days' + TIME '09:00:00', 
 CURRENT_DATE + INTERVAL '31 days' + TIME '18:00:00', 
 CURRENT_DATE + INTERVAL '28 days', 
 60, 
 4, 
 4, 
 1); -- REGISTRAZIONI_APERTE

-- Hackathon 5: REGISTRAZIONI_APERTE
INSERT INTO hackathon (hackathon_id, titolo, descrizione, sede, data_inizio, data_fine, data_chiusura_registrazioni, max_iscritti, max_dimensione_team, organizzatore_id, status_id) VALUES
(5, 'GameDev Marathon',
 'Crea il prossimo videogioco di successo in 48 ore!',
 'Bologna - Università', 
 CURRENT_DATE + INTERVAL '15 days' + TIME '09:00:00', 
 CURRENT_DATE + INTERVAL '16 days' + TIME '18:00:00', 
 CURRENT_DATE + INTERVAL '13 days', 
 20, 
 3, 
 5, 
 1); -- REGISTRAZIONI_APERTE

-- ==================================================
-- CREAZIONE PROBLEMI PER ALCUNI HACKATHON
-- ==================================================

INSERT INTO problema (hackathon_id, titolo, descrizione, pubblicato_da) VALUES
(1, 'Riconoscimento Automatico Emozioni', 'Sviluppare un sistema AI che riconosca le emozioni umane da video in tempo reale', 1),
(1, 'Assistente Virtuale Intelligente', 'Creare un chatbot che possa aiutare gli studenti universitari', 1),
(2, 'DeFi Lending Platform', 'Sviluppare una piattaforma di prestiti decentralizzata su blockchain', 2),
(3, 'Smart Recycling System', 'Sistema IoT per ottimizzare la raccolta differenziata', 3);

-- ==================================================
-- INVITI GIUDICI
-- ==================================================

-- Hackathon 1 (TERMINATO) - tutti accettati
INSERT INTO giudici_hackathon (hackathon_id, giudice_id, invitato_da, stato_invito_id) VALUES
(1, 6, 1, 2), -- prof.ferrari - ACCEPTED
(1, 7, 1, 2), -- prof.russo - ACCEPTED
(1, 8, 1, 2), -- prof.romano - ACCEPTED
(1, 11, 1, 2); -- dott.fontana - ACCEPTED

-- Hackathon 2 (IN_CORSO) - misti
INSERT INTO giudici_hackathon (hackathon_id, giudice_id, invitato_da, stato_invito_id) VALUES
(2, 6, 2, 2), -- prof.ferrari - ACCEPTED
(2, 9, 2, 2), -- prof.gallo - ACCEPTED
(2, 12, 2, 1), -- dott.ricci - PENDING
(2, 13, 2, 2); -- ing.lombardi - ACCEPTED

-- Hackathon 3 (REGISTRAZIONI_CHIUSE) - alcuni pending
INSERT INTO giudici_hackathon (hackathon_id, giudice_id, invitato_da, stato_invito_id) VALUES
(3, 7, 3, 2), -- prof.russo - ACCEPTED
(3, 10, 3, 1), -- prof.costa - PENDING
(3, 14, 3, 2), -- ing.moretti - ACCEPTED
(3, 15, 3, 3); -- arch.barbieri - DECLINED

-- Hackathon 4 (REGISTRAZIONI_APERTE) - tutti pending
INSERT INTO giudici_hackathon (hackathon_id, giudice_id, invitato_da, stato_invito_id) VALUES
(4, 6, 4, 1), -- prof.ferrari - PENDING
(4, 8, 4, 1), -- prof.romano - PENDING
(4, 11, 4, 1); -- dott.fontana - PENDING

-- ==================================================
-- CREAZIONE TEAM
-- ==================================================

-- Team per Hackathon 1 (TERMINATO)
INSERT INTO team (nome, hackathon_id, definitivo) VALUES
('AI Wizards', 1, true),
('Neural Network Ninjas', 1, true),
('Deep Learning Dragons', 1, true),
('Machine Learning Masters', 1, true);

-- Team per Hackathon 2 (IN_CORSO)
INSERT INTO team (nome, hackathon_id, definitivo) VALUES
('Blockchain Builders', 2, true),
('Crypto Crusaders', 2, true),
('DeFi Developers', 2, false), -- Non definitivo
('Smart Contract Squad', 2, true);

-- Team per Hackathon 3 (REGISTRAZIONI_CHIUSE)
INSERT INTO team (nome, hackathon_id, definitivo) VALUES
('Green Innovators', 3, false),
('Eco Warriors', 3, false),
('Sustainable Solutions', 3, false);

-- Team per Hackathon 4 (REGISTRAZIONI_APERTE)
INSERT INTO team (nome, hackathon_id, definitivo) VALUES
('Health Hackers', 4, false),
('MedTech Mavens', 4, false);

-- Team per Hackathon 5 (REGISTRAZIONI_APERTE - quasi pieno)
INSERT INTO team (nome, hackathon_id, definitivo) VALUES
('Pixel Pirates', 5, false),
('Game Changers', 5, false),
('Code Warriors', 5, false),
('Digital Dreams', 5, false),
('Arcade Legends', 5, false);

-- ==================================================
-- REGISTRAZIONI E MEMBRI TEAM
-- ==================================================

BEGIN;

-- DISABILITA TEMPORANEAMENTE I TRIGGER
ALTER TABLE registrazioni DISABLE TRIGGER ALL;
ALTER TABLE membri_team DISABLE TRIGGER ALL;

-- Hackathon 1 (TERMINATO)
INSERT INTO registrazioni (utente_id, hackathon_id, team_id) VALUES
(16, 1, 1), (17, 1, 1), (18, 1, 1), (19, 1, 1),
(20, 1, 2), (21, 1, 2), (22, 1, 2), (23, 1, 2),
(24, 1, 3), (25, 1, 3), (26, 1, 3),
(27, 1, 4), (28, 1, 4), (29, 1, 4), (30, 1, 4);

INSERT INTO registrazioni (utente_id, hackathon_id) VALUES
(31, 1), (32, 1);

INSERT INTO membri_team (team_id, utente_id, ruolo_team) VALUES
(1, 16, 'LEADER'), (1, 17, 'MEMBRO'), (1, 18, 'MEMBRO'), (1, 19, 'MEMBRO'),
(2, 20, 'LEADER'), (2, 21, 'MEMBRO'), (2, 22, 'MEMBRO'), (2, 23, 'MEMBRO'),
(3, 24, 'LEADER'), (3, 25, 'MEMBRO'), (3, 26, 'MEMBRO'),
(4, 27, 'LEADER'), (4, 28, 'MEMBRO'), (4, 29, 'MEMBRO'), (4, 30, 'MEMBRO');

-- Hackathon 2 (IN_CORSO)
INSERT INTO registrazioni (utente_id, hackathon_id, team_id) VALUES
(33, 2, 5), (34, 2, 5), (35, 2, 5),
(36, 2, 6), (37, 2, 6),
(38, 2, 7),
(39, 2, 8), (40, 2, 8), (41, 2, 8);

INSERT INTO registrazioni (utente_id, hackathon_id) VALUES
(42, 2), (43, 2), (44, 2);

INSERT INTO membri_team (team_id, utente_id, ruolo_team) VALUES
(5, 33, 'LEADER'), (5, 34, 'MEMBRO'), (5, 35, 'MEMBRO'),
(6, 36, 'LEADER'), (6, 37, 'MEMBRO'),
(7, 38, 'LEADER'),
(8, 39, 'LEADER'), (8, 40, 'MEMBRO'), (8, 41, 'MEMBRO');

-- Hackathon 3 (REGISTRAZIONI_CHIUSE)
INSERT INTO registrazioni (utente_id, hackathon_id, team_id) VALUES
(45, 3, 9), (46, 3, 9),
(47, 3, 10);

INSERT INTO registrazioni (utente_id, hackathon_id) VALUES
(48, 3), (49, 3), (50, 3), (16, 3), (17, 3);

INSERT INTO membri_team (team_id, utente_id, ruolo_team) VALUES
(9, 45, 'LEADER'), (9, 46, 'MEMBRO'),
(10, 47, 'LEADER');

-- Hackathon 4 (REGISTRAZIONI_APERTE)
INSERT INTO registrazioni (utente_id, hackathon_id, team_id) VALUES
(18, 4, 12), (19, 4, 12);

INSERT INTO registrazioni (utente_id, hackathon_id) VALUES
(20, 4), (21, 4), (22, 4);

INSERT INTO membri_team (team_id, utente_id, ruolo_team) VALUES
(12, 18, 'LEADER'), (12, 19, 'MEMBRO');

-- Hackathon 5 (REGISTRAZIONI_APERTE)
INSERT INTO registrazioni (utente_id, hackathon_id, team_id) VALUES
(23, 5, 14), (24, 5, 14), (25, 5, 14),
(26, 5, 15), (27, 5, 15), (28, 5, 15),
(29, 5, 16), (30, 5, 16), (31, 5, 16),
(32, 5, 17), (33, 5, 17),
(34, 5, 18), (35, 5, 18), (36, 5, 18);

INSERT INTO registrazioni (utente_id, hackathon_id) VALUES
(37, 5), (38, 5), (39, 5);

INSERT INTO membri_team (team_id, utente_id, ruolo_team) VALUES
(14, 23, 'LEADER'), (14, 24, 'MEMBRO'), (14, 25, 'MEMBRO'),
(15, 26, 'LEADER'), (15, 27, 'MEMBRO'), (15, 28, 'MEMBRO'),
(16, 29, 'LEADER'), (16, 30, 'MEMBRO'), (16, 31, 'MEMBRO'),
(17, 32, 'LEADER'), (17, 33, 'MEMBRO'),
(18, 34, 'LEADER'), (18, 35, 'MEMBRO'), (18, 36, 'MEMBRO');

-- RIABILITA I TRIGGER
ALTER TABLE registrazioni ENABLE TRIGGER ALL;
ALTER TABLE membri_team ENABLE TRIGGER ALL;

COMMIT;

-- ==================================================
-- INVITI TEAM
-- ==================================================

BEGIN;

-- DISABILITA TEMPORANEAMENTE I TRIGGER
ALTER TABLE inviti_team DISABLE TRIGGER ALL;

-- Inviti per Hackathon 2 (IN_CORSO)
-- Team 6 cerca un terzo membro
INSERT INTO inviti_team (team_id, invitante_id, invitato_id, messaggio_motivazionale, stato_invito_id) VALUES
(6, 19, 25, 'Ciao! Stiamo cercando un esperto di smart contracts, ti va di unirti?', 1), -- PENDING
(6, 19, 26, 'Abbiamo bisogno di un developer React per il frontend!', 3); -- DECLINED

-- Team 7 cerca membri
INSERT INTO inviti_team (team_id, invitante_id, invitato_id, messaggio_motivazionale, stato_invito_id) VALUES
(7, 21, 25, 'Stiamo costruendo una DeFi app innovativa, unisciti a noi!', 1), -- PENDING
(7, 21, 27, 'Cerchiamo sviluppatori appassionati di blockchain', 1); -- PENDING

-- Inviti per Hackathon 3 (REGISTRAZIONI_CHIUSE)
-- Team 9 cerca di completarsi
INSERT INTO inviti_team (team_id, invitante_id, invitato_id, messaggio_motivazionale, stato_invito_id) VALUES
(9, 28, 31, 'Unisciti ai Green Innovators!', 2), -- ACCEPTED (ma non ancora nel team per test)
(9, 28, 32, 'Cerchiamo esperti IoT per il nostro progetto', 1), -- PENDING
(9, 28, 33, 'Ti interessa la sostenibilità? Vieni con noi!', 1); -- PENDING

-- Inviti per Hackathon 4
INSERT INTO inviti_team (team_id, invitante_id, invitato_id, messaggio_motivazionale, stato_invito_id) VALUES
(12, 36, 38, 'Stiamo sviluppando una app per la telemedicina', 1), -- PENDING
(12, 36, 39, 'Cerchiamo un data scientist per analisi mediche', 2); -- ACCEPTED

-- RIABILITA I TRIGGER
ALTER TABLE inviti_team ENABLE TRIGGER ALL;

COMMIT;

-- ==================================================
-- PROGRESSI CARICATI DAI TEAM
-- ==================================================

BEGIN;

-- DISABILITA TEMPORANEAMENTE I TRIGGER
ALTER TABLE progressi DISABLE TRIGGER ALL;

-- Progressi Hackathon 1 (TERMINATO)
INSERT INTO progressi (team_id, titolo, descrizione, documento_path, documento_nome, caricato_da) VALUES
(1, 'Architettura del Sistema', 'Documento di design dell''architettura AI', '/uploads/h1/team1/architecture.pdf', 'architecture.pdf', 16),
(1, 'Prototipo Alpha', 'Prima versione funzionante del riconoscimento emozioni', '/uploads/h1/team1/prototype_v1.zip', 'prototype_v1.zip', 17),
(1, 'Presentazione Finale', 'Slide della presentazione finale', '/uploads/h1/team1/presentation.pptx', 'AI_Wizards_Final.pptx', 16),
(2, 'Modello ML Training', 'Dataset e codice per il training', '/uploads/h1/team2/ml_model.zip', 'neural_network_model.zip', 20),
(2, 'Demo Video', 'Video dimostrativo del chatbot', '/uploads/h1/team2/demo.mp4', 'chatbot_demo.mp4', 21),
(3, 'Research Paper', 'Documento di ricerca sulle tecniche utilizzate', '/uploads/h1/team3/research.pdf', 'deep_learning_research.pdf', 24),
(4, 'Codice Sorgente', 'Repository completo del progetto', '/uploads/h1/team4/source.zip', 'ml_masters_source.zip', 27);

-- Progressi Hackathon 2 (IN_CORSO)
INSERT INTO progressi (team_id, titolo, descrizione, documento_path, documento_nome, caricato_da) VALUES
(5, 'Smart Contract v1', 'Prima versione dello smart contract', '/uploads/h2/team5/contract.sol', 'lending_contract.sol', 16),
(5, 'Frontend React', 'Interfaccia utente della DApp', '/uploads/h2/team5/frontend.zip', 'dapp_frontend.zip', 17),
(6, 'Whitepaper', 'Documento tecnico del progetto', '/uploads/h2/team6/whitepaper.pdf', 'crypto_crusaders_wp.pdf', 19),
(8, 'Audit Security', 'Report di sicurezza smart contract', '/uploads/h2/team8/audit.pdf', 'security_audit.pdf', 22);

-- RIABILITA I TRIGGER
ALTER TABLE progressi ENABLE TRIGGER ALL;

COMMIT;

-- ==================================================
-- COMMENTI DEI GIUDICI
-- ==================================================

-- Commenti Hackathon 1 (TERMINATO)
INSERT INTO commenti (progresso_id, giudice_id, testo) VALUES
(1, 6, 'Ottima architettura, ben strutturata e scalabile. Complimenti per l''approccio modulare.'),
(1, 7, 'Mi piace l''uso dei design pattern. Suggerirei di documentare meglio le API.'),
(2, 6, 'Il prototipo funziona bene! Performance da migliorare ma l''idea è solida.'),
(2, 8, 'Impressionante per essere una prima versione. Il riconoscimento è accurato.'),
(3, 6, 'Presentazione chiara e coinvolgente. Ottimo lavoro di team!'),
(4, 7, 'Approccio innovativo al training del modello. Dataset ben curato.'),
(5, 8, 'Demo efficace, mostra bene le potenzialità del chatbot.'),
(6, 11, 'Research di alto livello, pubblicabile su conference internazionali.');

-- Commenti Hackathon 2 (IN_CORSO)
INSERT INTO commenti (progresso_id, giudice_id, testo) VALUES
(8, 6, 'Smart contract ben scritto, attenzione alle gas fees.'),
(9, 9, 'UI/UX molto intuitiva, ottimo lavoro sul frontend!'),
(10, 13, 'Whitepaper completo e dettagliato. Tokenomics da rivedere.');

-- ==================================================
-- VOTI (solo per team definitivi)
-- ==================================================

-- Voti Hackathon 1 (TERMINATO) - tutti i team sono votabili
INSERT INTO voti (hackathon_id, team_id, giudice_id, valore, criteri_valutazione) VALUES
(1, 1, 6, 9, 'Innovazione: 10/10, Implementazione: 9/10, Presentazione: 8/10'),
(1, 1, 7, 8, 'Ottimo lavoro complessivo, margini di miglioramento sulla documentazione'),
(1, 1, 8, 9, 'Progetto molto promettente con applicazioni reali'),
(1, 1, 11, 10, 'Eccellente! Uno dei migliori progetti che ho visto'),
(1, 2, 6, 7, 'Buona esecuzione ma manca originalità'),
(1, 2, 7, 8, 'Solido progetto, ben implementato'),
(1, 2, 8, 7, 'Chatbot funzionale ma features limitate'),
(1, 2, 11, 8, 'Buon lavoro di squadra e presentazione'),
(1, 3, 6, 8, 'Approccio research-oriented interessante'),
(1, 3, 7, 9, 'Eccellente dal punto di vista accademico'),
(1, 3, 8, 8, 'Implementazione da migliorare ma teoria solida'),
(1, 3, 11, 7, 'Manca un po'' di praticità ma ottima ricerca'),
(1, 4, 6, 10, 'Progetto completo e ben eseguito!'),
(1, 4, 7, 9, 'Ottima integrazione di diverse tecnologie ML'),
(1, 4, 8, 10, 'Il migliore del lotto! Complimenti'),
(1, 4, 11, 9, 'Professionale e ready-to-market');

-- Voti Hackathon 2 (IN_CORSO) - solo team definitivi (5, 6, 8)
INSERT INTO voti (hackathon_id, team_id, giudice_id, valore, criteri_valutazione) VALUES
(2, 5, 9, 9, 'Ottima implementazione DeFi, molto promettente'),
(2, 5, 13, 8, 'Buon bilanciamento tra complessità e usabilità'),
(2, 6, 6, 7, 'Progetto interessante ma incompleto'),
(2, 6, 9, 6, 'Manca il terzo membro si sente, ma buon effort'),
(2, 8, 6, 9, 'Security-first approach eccellente'),
(2, 8, 9, 10, 'Il migliore finora! Audit professionale'),
(2, 8, 13, 9, 'Codice pulito e ben documentato');

-- ==================================================
-- RESET DELLE SEQUENZE DOPO POPOLAMENTO MANUALE
-- ==================================================

SELECT setval('utenti_utente_id_seq', (SELECT MAX(utente_id) FROM utenti));
SELECT setval('hackathon_hackathon_id_seq', (SELECT MAX(hackathon_id) FROM hackathon));
SELECT setval('team_team_id_seq', (SELECT MAX(team_id) FROM team));