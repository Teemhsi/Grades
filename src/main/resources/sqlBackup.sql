DROP DATABASE IF EXISTS `grades`;
CREATE DATABASE `grades` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `grades`;

-- UTENTI
DROP TABLE IF EXISTS `Utenti`;
CREATE TABLE `Utenti` (
  `id_utente` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `ruolo` ENUM('studente', 'docente') NOT NULL,
  PRIMARY KEY (`id_utente`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `Utenti` VALUES
(1, 'mario.rossi@uni.it', 'password1', 'docente'),
(2, 'laura.bianchi@uni.it', 'password2', 'docente'),
(3, 'anna.verdi@studenti.uni.it', 'password3', 'studente'),
(4, 'carlo.neri@studenti.uni.it', 'password4', 'studente'),
(5, 'dario.gialli@studenti.uni.it', 'password5', 'studente'),
(6, 'marco.blu@studenti.uni.it', 'password6', 'studente'),
(7, 'sara.viola@studenti.uni.it', 'password7', 'studente'),
(8, 'luca.arancio@studenti.uni.it', 'password8', 'studente'),
(9, 'paolo.rosa@uni.it', 'password9', 'docente'),
(10, 'chiara.azzurra@uni.it', 'password10', 'docente'),
(11, 'elena.marrone@studenti.uni.it', 'password11', 'studente'),
(12, 'fabio.celeste@studenti.uni.it', 'password12', 'studente'),
(13, 'giulia.beige@studenti.uni.it', 'password13', 'studente'),
(14, 'roberto.indaco@studenti.uni.it', 'password14', 'studente'),
(15, 'alessia.carminio@studenti.uni.it', 'password15', 'studente'),
(16, 'davide.magenta@studenti.uni.it', 'password16', 'studente'),
(17, 'monica.lime@studenti.uni.it', 'password17', 'studente'),
(18, 'stefano.cobalto@studenti.uni.it', 'password18', 'studente'),
(19, 'claudia.bordeaux@studenti.uni.it', 'password19', 'studente'),
(20, 'antonio.acquamarina@studenti.uni.it', 'password20', 'studente'),
(21, 'valentina.ocra@studenti.uni.it', 'password21', 'studente'),
(22, 'francesco.lilla@studenti.uni.it', 'password22', 'studente'),
(23, 'greta.smeraldo@studenti.uni.it', 'password23', 'studente'),
(24, 'leonardo.granata@studenti.uni.it', 'password24', 'studente'),
(25, 'silvia.turchese@studenti.uni.it', 'password25', 'studente'),
(26, 'martina.porpora@uni.it', 'password26', 'docente'),
(27, 'claudio.seppia@uni.it', 'password27', 'docente'),
(28, 'renata.sabbia@uni.it', 'password28', 'docente'),
(29, 'alberto.corallo@uni.it', 'password29', 'docente'),
(30, 'barbara.ambra@uni.it', 'password30', 'docente'),
(31, 'matteo.grigio@studenti.uni.it', 'password31', 'studente');

-- STUDENTI
DROP TABLE IF EXISTS `Studenti`;
CREATE TABLE `Studenti` (
  `id_utente` INT NOT NULL,
  `matricola` VARCHAR(20) NOT NULL,
  `nome` VARCHAR(50),
  `cognome` VARCHAR(50),
  `corso_di_laurea` VARCHAR(100),
  PRIMARY KEY (`id_utente`),
  UNIQUE (`matricola`),
  FOREIGN KEY (`id_utente`) REFERENCES `Utenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `Studenti` VALUES
(3, 'S1001', 'Anna', 'Verdi', 'Informatica'),
(4, 'S1002', 'Carlo', 'Neri', 'Ingegneria'),
(5, 'S1003', 'Dario', 'Gialli', 'Matematica'),
(6, 'S1004', 'Marco', 'Blu', 'Fisica'),
(7, 'S1005', 'Sara', 'Viola', 'Chimica'),
(8, 'S1006', 'Luca', 'Arancio', 'Informatica'),
(11, 'S1007', 'Elena', 'Marrone', 'Informatica'),
(12, 'S1008', 'Fabio', 'Celeste', 'Ingegneria'),
(13, 'S1009', 'Giulia', 'Beige', 'Matematica'),
(14, 'S1010', 'Roberto', 'Indaco', 'Fisica'),
(15, 'S1011', 'Alessia', 'Carminio', 'Chimica'),
(16, 'S1012', 'Davide', 'Magenta', 'Informatica'),
(17, 'S1013', 'Monica', 'Lime', 'Ingegneria'),
(18, 'S1014', 'Stefano', 'Cobalto', 'Matematica'),
(19, 'S1015', 'Claudia', 'Bordeaux', 'Fisica'),
(20, 'S1016', 'Antonio', 'Acquamarina', 'Chimica'),
(21, 'S1017', 'Valentina', 'Ocra', 'Informatica'),
(22, 'S1018', 'Francesco', 'Lilla', 'Ingegneria'),
(23, 'S1019', 'Greta', 'Smeraldo', 'Matematica'),
(24, 'S1020', 'Leonardo', 'Granata', 'Fisica'),
(25, 'S1021', 'Silvia', 'Turchese', 'Chimica'),
(31, 'S1022', 'Matteo', 'Grigio', 'Informatica');

-- DOCENTI
DROP TABLE IF EXISTS `Docenti`;
CREATE TABLE `Docenti` (
  `id_utente` INT NOT NULL,
  `nome` VARCHAR(50),
  `cognome` VARCHAR(50),
  PRIMARY KEY (`id_utente`),
  FOREIGN KEY (`id_utente`) REFERENCES `Utenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `Docenti` VALUES
(1, 'Mario', 'Rossi'),
(2, 'Laura', 'Bianchi'),
(9, 'Paolo', 'Rosa'),
(10, 'Chiara', 'Azzurra'),
(26, 'Martina', 'Porpora'),
(27, 'Claudio', 'Seppia'),
(28, 'Renata', 'Sabbia'),
(29, 'Alberto', 'Corallo'),
(30, 'Barbara', 'Ambra');

-- CORSI
DROP TABLE IF EXISTS `Corsi`;
CREATE TABLE `Corsi` (
  `id_corso` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `id_docente` INT NOT NULL,
  PRIMARY KEY (`id_corso`),
  FOREIGN KEY (`id_docente`) REFERENCES `Docenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `Corsi` VALUES
(1, 'Algoritmi', 1),
(2, 'Analisi Matematica', 2),
(3, 'Fisica Generale', 9),
(4, 'Chimica Organica', 10),
(5, 'Basi di Dati', 1),
(6, 'Programmazione', 1),
(7, 'Sistemi Operativi', 26),
(8, 'Reti di Calcolatori', 10),
(9, 'Geometria', 2),
(10, 'Calcolo Numerico', 27),
(11, 'Fisica Quantistica', 9),
(12, 'Meccanica Razionale', 28),
(13, 'Chimica Analitica', 10),
(14, 'Biochimica', 29),
(15, 'Statistica', 30),
(16, 'Intelligenza Artificiale', 1),
(17, 'Machine Learning', 26),
(18, 'Elettronica', 27),
(19, 'Architettura degli Elaboratori', 9),
(20, 'Automazione', 28);

-- APPELLI
DROP TABLE IF EXISTS `Appelli`;
CREATE TABLE `Appelli` (
  `id_appello` INT NOT NULL AUTO_INCREMENT,
  `id_corso` INT NOT NULL,
  `data_appello` DATE NOT NULL,
  PRIMARY KEY (`id_appello`),
  FOREIGN KEY (`id_corso`) REFERENCES `Corsi` (`id_corso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add index for ordering appelli by corso and date
CREATE INDEX idx_corso_data ON Appelli (id_corso, data_appello);

INSERT INTO `Appelli` VALUES
(1, 1, '2025-02-01'),
(2, 1, '2025-03-01'),
(3, 2, '2025-02-15'),
(4, 3, '2025-02-20'),
(5, 4, '2025-03-15'),
(6, 5, '2025-04-01'),
(7, 1, '2025-05-10'),
(8, 2, '2025-06-15'),
(9, 2, '2025-07-01'),
(10, 3, '2025-07-10'),
(11, 4, '2025-07-15'),
(12, 5, '2025-07-20'),
(13, 6, '2025-07-25'),
(14, 7, '2025-08-01'),
(15, 8, '2025-08-05'),
(16, 9, '2025-08-10'),
(17, 10, '2025-08-15'),
(18, 11, '2025-09-01'),
(19, 12, '2025-09-05'),
(20, 13, '2025-09-10'),
(21, 14, '2025-09-15'),
(22, 15, '2025-09-20'),
(23, 16, '2025-10-01'),
(24, 17, '2025-10-05'),
(25, 18, '2025-10-10'),
(26, 19, '2025-10-15'),
(27, 20, '2025-10-20'),
(28, 1, '2025-11-01'),
(29, 2, '2025-11-05'),
(30, 3, '2025-11-10');

-- ISCRIZIONI AI CORSI
DROP TABLE IF EXISTS `IscrizioniCorsi`;
CREATE TABLE `IscrizioniCorsi` (
  `id_studente` INT NOT NULL,
  `id_corso` INT NOT NULL,
  `anno_accademico` VARCHAR(9) NOT NULL,
  `data_iscrizione` DATE NOT NULL,
  PRIMARY KEY (`id_studente`, `id_corso`, `anno_accademico`),
  FOREIGN KEY (`id_studente`) REFERENCES `Studenti` (`id_utente`) ON DELETE CASCADE,
  FOREIGN KEY (`id_corso`) REFERENCES `Corsi` (`id_corso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `IscrizioniCorsi` VALUES
-- Algoritmi (corso 1)
(3, 1, '2024/2025', '2024-09-15'),  -- Anna Verdi
(4, 1, '2024/2025', '2024-09-15'),  -- Carlo Neri
(5, 1, '2024/2025', '2024-09-15'),  -- Dario Gialli
(6, 1, '2024/2025', '2024-09-15'),  -- Marco Blu
(7, 1, '2024/2025', '2024-09-15'),  -- Sara Viola
(11, 1, '2024/2025', '2024-09-15'), -- Elena Marrone
(12, 1, '2024/2025', '2024-09-15'), -- Fabio Celeste
(17, 1, '2024/2025', '2024-09-15'), -- Monica Lime
(24, 1, '2024/2025', '2024-09-15'), -- Leonardo Granata
(25, 1, '2024/2025', '2024-09-15'), -- Silvia Turchese

-- Analisi Matematica (corso 2)
(5, 2, '2024/2025', '2024-09-15'),  -- Dario Gialli
(6, 2, '2024/2025', '2024-09-15'),  -- Marco Blu
(8, 2, '2024/2025', '2024-09-15'),  -- Luca Arancio
(13, 2, '2024/2025', '2024-09-15'), -- Giulia Beige
(18, 2, '2024/2025', '2024-09-15'), -- Stefano Cobalto
(23, 2, '2024/2025', '2024-09-15'), -- Greta Smeraldo

-- Fisica Generale (corso 3)
(3, 3, '2024/2025', '2024-09-15'),  -- Anna Verdi
(7, 3, '2024/2025', '2024-09-15'),  -- Sara Viola
(14, 3, '2024/2025', '2024-09-15'), -- Roberto Indaco
(17, 3, '2024/2025', '2024-09-15'), -- Monica Lime
(22, 3, '2024/2025', '2024-09-15'), -- Francesco Lilla

-- Chimica Organica (corso 4)
(4, 4, '2024/2025', '2024-09-15'),  -- Carlo Neri
(8, 4, '2024/2025', '2024-09-15'),  -- Luca Arancio
(15, 4, '2024/2025', '2024-09-15'), -- Alessia Carminio
(16, 4, '2024/2025', '2024-09-15'), -- Davide Magenta
(21, 4, '2024/2025', '2024-09-15'), -- Valentina Ocra

-- Basi di Dati (corso 5)
(5, 5, '2024/2025', '2024-09-15'),  -- Dario Gialli
(8, 5, '2024/2025', '2024-09-15'),  -- Luca Arancio
(16, 5, '2024/2025', '2024-09-15'), -- Davide Magenta

-- Programmazione (corso 6)
(23, 6, '2024/2025', '2024-09-15'), -- Greta Smeraldo

-- Sistemi Operativi (corso 7)
(24, 7, '2024/2025', '2024-09-15'), -- Leonardo Granata

-- Reti di Calcolatori (corso 8)
(25, 8, '2024/2025', '2024-09-15'), -- Silvia Turchese

-- Biochimica (corso 14)
(15, 14, '2024/2025', '2024-09-15'), -- Alessia Carminio
(16, 14, '2024/2025', '2024-09-15'), -- Davide Magenta

-- Statistica (corso 15)
(17, 15, '2024/2025', '2024-09-15'), -- Monica Lime

-- Intelligenza Artificiale (corso 16)
(18, 16, '2024/2025', '2024-09-15'), -- Stefano Cobalto

-- Machine Learning (corso 17)
(19, 17, '2024/2025', '2024-09-15'), -- Claudia Bordeaux

-- Elettronica (corso 18)
(19, 18, '2024/2025', '2024-09-15'), -- Claudia Bordeaux

-- Add some additional enrollments for courses without exams yet or without student registrations
(3, 15, '2024/2025', '2024-09-10'),  -- Anna Verdi enrolled in Statistics (no exam scheduled)
(31, 5, '2024/2025', '2024-09-20'),  -- Matteo Grigio enrolled in Basi di Dati
(11, 14, '2024/2025', '2024-09-17'), -- Elena Marrone enrolled in Biochemistry
(12, 13, '2024/2025', '2024-09-18'), -- Fabio Celeste enrolled in Analytical Chemistry
(13, 12, '2024/2025', '2024-09-19'), -- Giulia Beige enrolled in Rational Mechanics
(14, 11, '2024/2025', '2024-09-10'); -- Roberto Indaco enrolled in Quantum Physics

-- ISCRIZIONI
DROP TABLE IF EXISTS `Iscrizioni`;
CREATE TABLE `Iscrizioni` (
  `id_studente` INT NOT NULL,
  `id_appello` INT NOT NULL,
  `voto` ENUM('', 'Assente', 'Rimandato', 'Riprovato',
              '18','19','20','21','22','23','24','25','26','27','28','29','30','30 e lode') DEFAULT '',
  `stato_valutazione` ENUM('Non inserito', 'Inserito', 'Pubblicato', 'Rifiutato', 'Verbalizzato') NOT NULL DEFAULT 'Non inserito',
  PRIMARY KEY (`id_studente`, `id_appello`),
  FOREIGN KEY (`id_studente`) REFERENCES `Studenti` (`id_utente`) ON DELETE CASCADE,
  FOREIGN KEY (`id_appello`) REFERENCES `Appelli` (`id_appello`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `Iscrizioni` VALUES
-- Appello 1 (Algoritmi - 2025-02-01)
(3, 1, '30 e lode', 'Pubblicato'),  -- Anna Verdi
(4, 1, '22', 'Verbalizzato'),         -- Carlo Neri
(5, 1, '19', 'Verbalizzato'),         -- Dario Gialli
(6, 1, '28', 'Verbalizzato'),         -- Marco Blu
(11, 1, '24', 'Verbalizzato'),        -- Elena Marrone
(25, 1, '27', 'Verbalizzato'),        -- Silvia Turchese

-- Appello 2 (Algoritmi - 2025-03-01)
(3, 2, 'Riprovato', 'Verbalizzato'),  -- Anna Verdi
(4, 2, 'Rimandato', 'Inserito'),      -- Carlo Neri
(7, 2, '25', 'Verbalizzato'),         -- Sara Viola
(12, 2, '27', 'Verbalizzato'),        -- Fabio Celeste
(24, 2, '30', 'Verbalizzato'),        -- Leonardo Granata

-- Appello 3 (Analisi Matematica - 2025-02-15)
(5, 3, 'Assente', 'Inserito'),        -- Dario Gialli
(6, 3, '22', 'Verbalizzato'),         -- Marco Blu
(8, 3, '27', 'Verbalizzato'),         -- Luca Arancio
(13, 3, '19', 'Verbalizzato'),        -- Giulia Beige
(23, 3, '28', 'Verbalizzato'),        -- Greta Smeraldo

-- Appello 4 (Fisica Generale - 2025-02-20)
(3, 4, '30', 'Verbalizzato'),         -- Anna Verdi
(7, 4, 'Assente', 'Pubblicato'),      -- Sara Viola
(14, 4, '28', 'Verbalizzato'),        -- Roberto Indaco
(22, 4, '25', 'Verbalizzato'),        -- Francesco Lilla

-- Appello 5 (Chimica Organica - 2025-03-15)
(4, 5, '26', 'Verbalizzato'),         -- Carlo Neri
(8, 5, '23', 'Verbalizzato'),         -- Luca Arancio
(15, 5, '22', 'Verbalizzato'),        -- Alessia Carminio
(21, 5, '26', 'Verbalizzato'),        -- Valentina Ocra

-- Appello 6 (Basi di Dati - 2025-04-01)
(5, 6, '24', 'Verbalizzato'),         -- Dario Gialli
(8, 6, '30 e lode', 'Rifiutato'),     -- Luca Arancio
(16, 6, '26', 'Verbalizzato'),        -- Davide Magenta

-- Add more exam registrations
(4, 7, '27', 'Verbalizzato'),         -- Carlo Neri - Algoritmi (terzo appello)
(17, 7, '21', 'Verbalizzato'),        -- Monica Lime - Algoritmi (terzo appello)

(18, 8, '30', 'Verbalizzato'),        -- Stefano Cobalto - Analisi Matematica (secondo appello)

(17, 10, '24', 'Verbalizzato'),       -- Monica Lime - Fisica Generale (secondo appello)

(21, 11, '30 e lode', 'Verbalizzato'), -- Valentina Ocra - Chimica Organica (secondo appello)
(16, 11, '23', 'Verbalizzato'),        -- Davide Magenta - Chimica Organica (secondo appello)

(23, 13, '26', 'Verbalizzato'),       -- Greta Smeraldo - Programmazione

(24, 14, '27', 'Verbalizzato'),       -- Leonardo Granata - Sistemi Operativi

(25, 15, '24', 'Verbalizzato'),       -- Silvia Turchese - Reti di Calcolatori

(15, 21, '30', 'Verbalizzato'),       -- Alessia Carminio - Biochimica
(16, 21, '27', 'Verbalizzato'),       -- Davide Magenta - Biochimica

(17, 22, '25', 'Verbalizzato'),       -- Monica Lime - Statistica

(18, 23, '22', 'Verbalizzato'),       -- Stefano Cobalto - Intelligenza Artificiale

(19, 24, '28', 'Verbalizzato'),       -- Claudia Bordeaux - Machine Learning

(19, 25, '28', 'Verbalizzato');       -- Claudia Bordeaux - Elettronica

-- VERBALI - Modified table structure with datetime and codice_verbale
DROP TABLE IF EXISTS `Verbali`;
CREATE TABLE `Verbali` (
  `id_verbale` INT NOT NULL AUTO_INCREMENT,
  `codice_verbale` VARCHAR(50) NOT NULL, -- System-generated code
  `id_appello` INT NOT NULL,
  `data_creazione` DATETIME NOT NULL, -- Changed to DATETIME
  PRIMARY KEY (`id_verbale`),
  UNIQUE KEY (`codice_verbale`),
  FOREIGN KEY (`id_appello`) REFERENCES `Appelli` (`id_appello`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert into the modified Verbali table with codice_verbale and datetime
INSERT INTO `Verbali` VALUES
(1, 'VRB-ALG-20250210-001', 1, '2025-02-10 14:30:00'),
(2, 'VRB-ALG-20250305-001', 2, '2025-03-05 10:15:00'),
(3, 'VRB-ANM-20250301-001', 3, '2025-03-01 11:00:00'),
(4, 'VRB-FIS-20250310-001', 4, '2025-03-10 09:45:00'),
(5, 'VRB-CHI-20250401-001', 5, '2025-04-01 15:30:00'),
(6, 'VRB-BDD-20250420-001', 6, '2025-04-20 16:00:00'),
(7, 'VRB-ALG-20250525-001', 7, '2025-05-25 13:15:00'),
(8, 'VRB-ANM-20250620-001', 8, '2025-06-20 10:30:00'),
(9, 'VRB-FIS-20250715-001', 10, '2025-07-15 14:00:00'),
(10, 'VRB-CHI-20250720-001', 11, '2025-07-20 11:45:00'),
(11, 'VRB-PRG-20250730-001', 13, '2025-07-30 15:30:00'),
(12, 'VRB-SIS-20250810-001', 14, '2025-08-10 09:00:00'),
(13, 'VRB-RET-20250815-001', 15, '2025-08-15 14:30:00'),
(14, 'VRB-BIO-20250920-001', 21, '2025-09-20 10:45:00'),
(15, 'VRB-STA-20250925-001', 22, '2025-09-25 16:15:00'),
(16, 'VRB-INT-20251010-001', 23, '2025-10-10 13:30:00'),
(17, 'VRB-MAC-20251020-001', 24, '2025-10-20 11:30:00'),
(18, 'VRB-ELE-20251025-001', 25, '2025-10-25 15:45:00');

-- DETTAGLIO_VERBALE
DROP TABLE IF EXISTS `DettaglioVerbale`;
CREATE TABLE `DettaglioVerbale` (
  `id_verbale` INT NOT NULL,
  `id_studente` INT NOT NULL,
  `voto` ENUM('', 'Assente', 'Rimandato', 'Riprovato',
              '18','19','20','21','22','23','24','25','26','27','28','29','30','30 e lode') DEFAULT '',
  PRIMARY KEY (`id_verbale`, `id_studente`),
  FOREIGN KEY (`id_verbale`) REFERENCES `Verbali` (`id_verbale`) ON DELETE CASCADE,
  FOREIGN KEY (`id_studente`) REFERENCES `Studenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `DettaglioVerbale` VALUES
-- For appello 1 (id_verbale 1)
(1, 3, '30 e lode'), -- Anna Verdi - Algoritmi
(1, 4, '22'),        -- Carlo Neri - Algoritmi
(1, 5, '19'),        -- Dario Gialli - Algoritmi
(1, 6, '28'),        -- Marco Blu - Algoritmi
(1, 11, '24'),       -- Elena Marrone - Algoritmi
(1, 25, '27'),       -- Silvia Turchese - Algoritmi

-- For appello 2 (id_verbale 2)
(2, 3, 'Riprovato'), -- Anna Verdi - Algoritmi (secondo appello)
(2, 7, '25'),        -- Sara Viola - Algoritmi (secondo appello)
(2, 12, '27'),       -- Fabio Celeste - Algoritmi (secondo appello)
(2, 24, '30'),       -- Leonardo Granata - Algoritmi (secondo appello)

-- For appello 3 (id_verbale 3)
(3, 6, '22'),        -- Marco Blu - Analisi Matematica
(3, 8, '27'),        -- Luca Arancio - Analisi Matematica
(3, 13, '19'),       -- Giulia Beige - Analisi Matematica
(3, 23, '28'),       -- Greta Smeraldo - Analisi Matematica

-- For appello 4 (id_verbale 4)
(4, 3, '30'),        -- Anna Verdi - Fisica Generale
(4, 14, '28'),       -- Roberto Indaco - Fisica Generale
(4, 22, '25'),       -- Francesco Lilla - Fisica Generale

-- For appello 5 (id_verbale 5)
(5, 4, '26'),        -- Carlo Neri - Chimica Organica
(5, 8, '23'),        -- Luca Arancio - Chimica Organica
(5, 15, '22'),       -- Alessia Carminio - Chimica Organica
(5, 21, '26'),       -- Valentina Ocra - Chimica Organica

-- Continue with remaining verbalized exams
(6, 5, '24'),        -- Dario Gialli - Basi di Dati
(6, 16, '26'),       -- Davide Magenta - Basi di Dati

(7, 4, '27'),        -- Carlo Neri - Algoritmi (terzo appello)
(7, 17, '21'),       -- Monica Lime - Algoritmi (terzo appello)

(8, 18, '30'),       -- Stefano Cobalto - Analisi Matematica (secondo appello)

(9, 17, '24'),       -- Monica Lime - Fisica Generale (secondo appello)

(10, 16, '23'),      -- Davide Magenta - Chimica Organica (secondo appello)
(10, 21, '30 e lode'), -- Valentina Ocra - Chimica Organica (secondo appello)

(11, 23, '26'),      -- Greta Smeraldo - Programmazione

(12, 24, '27'),      -- Leonardo Granata - Sistemi Operativi

(13, 25, '24'),      -- Silvia Turchese - Reti di Calcolatori

(14, 15, '30'),      -- Alessia Carminio - Biochimica
(14, 16, '27'),      -- Davide Magenta - Biochimica

(15, 17, '25'),      -- Monica Lime - Statistica

(16, 18, '22'),      -- Stefano Cobalto - Intelligenza Artificiale

(17, 19, '28'),      -- Claudia Bordeaux - Machine Learning

(18, 19, '28');      -- Claudia Bordeaux - Elettronica