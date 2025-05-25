-- Use the grades database
USE `grades`;

-- Create a new verbale for exam session 9 (Analisi Matematica - 2025-07-01)
INSERT INTO `Verbali` VALUES
(19, 'VRB-ANM-20250710-001', 9, '2025-07-10 09:30:00');

-- Add details for verbale 19
INSERT INTO `DettaglioVerbale` VALUES
(19, 5, '22'),        -- Dario Gialli - Analisi Matematica (terzo appello)
(19, 13, '28'),       -- Giulia Beige - Analisi Matematica (terzo appello)
(19, 18, '26');       -- Stefano Cobalto - Analisi Matematica (terzo appello)

-- Create a new verbale for exam session 12 (Basi di Dati - 2025-07-20)
INSERT INTO `Verbali` VALUES
(20, 'VRB-BDD-20250725-001', 12, '2025-07-25 14:00:00');

-- Add details for verbale 20
INSERT INTO `DettaglioVerbale` VALUES
(20, 8, '30'),        -- Luca Arancio - Basi di Dati (after rejecting previous grade)
(20, 31, '30 e lode'); -- Matteo Grigio - Basi di Dati

-- Add more exam registrations with different grades and evaluation states
-- Appello 7 (Algoritmi - 2025-05-10)
INSERT INTO `Iscrizioni` VALUES
(7, 7, '18', 'Inserito'),             -- Sara Viola - grade inserted but not published
(25, 7, '21', 'Pubblicato'),          -- Silvia Turchese - grade published but not verbalized
(12, 7, '', 'Non inserito'),          -- Fabio Celeste - registered but no grade entered yet
(13, 7, '', 'Non inserito'),          -- Giulia Beige - registered but no grade entered yet

-- Appello 8 (Analisi Matematica - 2025-06-15)
(5, 8, 'Rimandato', 'Pubblicato'),    -- Dario Gialli - postponed and published
(13, 8, '27', 'Rifiutato'),           -- Giulia Beige - rejected grade
(19, 8, '', 'Non inserito'),          -- Claudia Bordeaux - registered but no grade entered yet
(20, 8, '', 'Non inserito'),          -- Antonio Acquamarina - registered but no grade entered yet

-- Appello 9 (Analisi Matematica - 2025-07-01)
(5, 9, '22', 'Verbalizzato'),         -- Dario Gialli - verbalized after being postponed
(13, 9, '28', 'Verbalizzato'),        -- Giulia Beige - verbalized after rejecting previous grade
(18, 9, '26', 'Verbalizzato'),        -- Stefano Cobalto - another verbalized grade

-- Appello 10 (Fisica Generale - 2025-07-10)
(7, 10, 'Riprovato', 'Pubblicato'),   -- Sara Viola - failed and published

-- Appello 11 (Chimica Organica - 2025-07-15)
(8, 11, '29', 'Verbalizzato'),        -- Luca Arancio - verbalized grade after rejecting previous one

-- Appello 12 (Basi di Dati - 2025-07-20)
(8, 12, '30', 'Verbalizzato'),        -- Luca Arancio - verbalized after rejecting previous grade of 30 e lode
(31, 12, '30 e lode', 'Verbalizzato'), -- Matteo Grigio - verbalized with highest grade

-- Appello 14 (Sistemi Operativi - 2025-08-01)
(31, 14, '23', 'Non inserito'),       -- Matteo Grigio - not yet inserted
(7, 14, 'Assente', 'Inserito'),       -- Sara Viola - absent, inserted

-- Appello 16 (Geometria - 2025-08-10)
(3, 16, '28', 'Inserito'),            -- Anna Verdi - inserted but not published
(7, 16, 'Assente', 'Pubblicato'),     -- Sara Viola - absent, published
(11, 16, '30', 'Pubblicato'),         -- Elena Marrone - published but not verbalized
(15, 16, '', 'Non inserito'),         -- Alessia Carminio - registered but no grade entered yet
(16, 16, '', 'Non inserito'),         -- Davide Magenta - registered but no grade entered yet

-- Appello 17 (Calcolo Numerico - 2025-08-15) - More Non inserito states
(5, 17, '', 'Non inserito'),          -- Dario Gialli - just registered
(13, 17, '', 'Non inserito'),         -- Giulia Beige - just registered
(18, 17, '', 'Non inserito'),         -- Stefano Cobalto - just registered
(19, 17, '', 'Non inserito'),         -- Claudia Bordeaux - just registered
(23, 17, '', 'Non inserito'),         -- Greta Smeraldo - just registered

-- Appello 18 (Fisica Quantistica - 2025-09-01) - Various Inserito states
(14, 18, '22', 'Inserito'),           -- Roberto Indaco - grade entered but not published
(6, 18, '19', 'Inserito'),            -- Marco Blu - grade entered but not published
(19, 18, '25', 'Inserito'),           -- Claudia Bordeaux - grade entered but not published
(5, 18, 'Assente', 'Inserito'),       -- Dario Gialli - marked absent, entered but not published
(7, 18, 'Rimandato', 'Inserito'),     -- Sara Viola - postponed, entered but not published

-- Appello 19 (Meccanica Razionale - 2025-09-05) - Mixed states
(13, 19, '27', 'Inserito'),           -- Giulia Beige - grade entered but not published
(3, 19, '', 'Non inserito'),          -- Anna Verdi - just registered
(5, 19, '', 'Non inserito'),          -- Dario Gialli - just registered
(8, 19, '21', 'Inserito'),            -- Luca Arancio - grade entered but not published
(20, 19, '', 'Non inserito'),         -- Antonio Acquamarina - just registered

-- Appello 20 (Chimica Analitica - 2025-09-10) - Various states for one exam
(12, 20, '24', 'Inserito'),           -- Fabio Celeste - grade entered but not published
(15, 20, '', 'Non inserito'),         -- Alessia Carminio - just registered
(16, 20, '', 'Non inserito'),         -- Davide Magenta - just registered
(20, 20, '23', 'Inserito'),           -- Antonio Acquamarina - grade entered but not published
(21, 20, '', 'Non inserito'),         -- Valentina Ocra - just registered

-- Appello 28 (Algoritmi - 2025-11-01) - Future exam with registrations
(3, 28, '', 'Non inserito'),          -- Anna Verdi - registered for future exam
(4, 28, '', 'Non inserito'),          -- Carlo Neri - registered for future exam
(7, 28, '', 'Non inserito'),          -- Sara Viola - registered for future exam
(11, 28, '', 'Non inserito'),         -- Elena Marrone - registered for future exam
(16, 28, '', 'Non inserito'),         -- Davide Magenta - registered for future exam
(24, 28, '', 'Non inserito'),         -- Leonardo Granata - registered for future exam
(25, 28, '', 'Non inserito'),         -- Silvia Turchese - registered for future exam
(31, 28, '', 'Non inserito');         -- Matteo Grigio - registered for future exam