-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: localhost    Database: grades
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.24.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Appelli`
--

DROP TABLE IF EXISTS `Appelli`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Appelli` (
                           `id_appello` int NOT NULL AUTO_INCREMENT,
                           `id_corso` int NOT NULL,
                           `data_appello` date NOT NULL,
                           PRIMARY KEY (`id_appello`),
                           KEY `idx_corso_data` (`id_corso`,`data_appello`),
                           CONSTRAINT `Appelli_ibfk_1` FOREIGN KEY (`id_corso`) REFERENCES `Corsi` (`id_corso`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Appelli`
--

LOCK TABLES `Appelli` WRITE;
/*!40000 ALTER TABLE `Appelli` DISABLE KEYS */;
INSERT INTO `Appelli` VALUES (1,1,'2025-02-01'),(2,1,'2025-03-01'),(7,1,'2025-05-10'),(28,1,'2025-11-01'),(3,2,'2025-02-15'),(8,2,'2025-06-15'),(9,2,'2025-07-01'),(29,2,'2025-11-05'),(4,3,'2025-02-20'),(10,3,'2025-07-10'),(30,3,'2025-11-10'),(5,4,'2025-03-15'),(11,4,'2025-07-15'),(6,5,'2025-04-01'),(12,5,'2025-07-20'),(13,6,'2025-07-25'),(14,7,'2025-08-01'),(15,8,'2025-08-05'),(16,9,'2025-08-10'),(17,10,'2025-08-15'),(18,11,'2025-09-01'),(19,12,'2025-09-05'),(20,13,'2025-09-10'),(21,14,'2025-09-15'),(22,15,'2025-09-20'),(23,16,'2025-10-01'),(24,17,'2025-10-05'),(25,18,'2025-10-10'),(26,19,'2025-10-15'),(27,20,'2025-10-20');
/*!40000 ALTER TABLE `Appelli` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Corsi`
--

DROP TABLE IF EXISTS `Corsi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Corsi` (
                         `id_corso` int NOT NULL AUTO_INCREMENT,
                         `nome` varchar(100) NOT NULL,
                         `id_docente` int NOT NULL,
                         PRIMARY KEY (`id_corso`),
                         KEY `id_docente` (`id_docente`),
                         CONSTRAINT `Corsi_ibfk_1` FOREIGN KEY (`id_docente`) REFERENCES `Docenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Corsi`
--

LOCK TABLES `Corsi` WRITE;
/*!40000 ALTER TABLE `Corsi` DISABLE KEYS */;
INSERT INTO `Corsi` VALUES (1,'Algoritmi',1),(2,'Analisi Matematica',2),(3,'Fisica Generale',9),(4,'Chimica Organica',10),(5,'Basi di Dati',1),(6,'Programmazione',1),(7,'Sistemi Operativi',26),(8,'Reti di Calcolatori',10),(9,'Geometria',2),(10,'Calcolo Numerico',27),(11,'Fisica Quantistica',9),(12,'Meccanica Razionale',28),(13,'Chimica Analitica',10),(14,'Biochimica',28),(15,'Statistica',30),(16,'Intelligenza Artificiale',1),(17,'Machine Learning',26),(18,'Elettronica',27),(19,'Architettura degli Elaboratori',9),(20,'Automazione',28);
/*!40000 ALTER TABLE `Corsi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DettaglioVerbale`
--

DROP TABLE IF EXISTS `DettaglioVerbale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DettaglioVerbale` (
                                    `id_verbale` int NOT NULL,
                                    `id_studente` int NOT NULL,
                                    `voto` enum('','Assente','Rimandato','Riprovato','18','19','20','21','22','23','24','25','26','27','28','29','30','30 e lode') DEFAULT '',
                                    PRIMARY KEY (`id_verbale`,`id_studente`),
                                    KEY `id_studente` (`id_studente`),
                                    CONSTRAINT `DettaglioVerbale_ibfk_1` FOREIGN KEY (`id_verbale`) REFERENCES `Verbali` (`id_verbale`) ON DELETE CASCADE,
                                    CONSTRAINT `DettaglioVerbale_ibfk_2` FOREIGN KEY (`id_studente`) REFERENCES `Studenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DettaglioVerbale`
--

LOCK TABLES `DettaglioVerbale` WRITE;
/*!40000 ALTER TABLE `DettaglioVerbale` DISABLE KEYS */;
INSERT INTO `DettaglioVerbale` VALUES (1,3,'30 e lode'),(1,4,'22'),(1,5,'19'),(1,6,'28'),(1,11,'24'),(1,25,'27'),(2,3,'Riprovato'),(2,7,'25'),(2,12,'27'),(2,24,'30'),(3,6,'22'),(3,8,'27'),(3,13,'19'),(3,23,'28'),(4,3,'30'),(4,14,'28'),(4,22,'25'),(5,4,'26'),(5,8,'23'),(5,15,'22'),(5,21,'26'),(6,5,'24'),(6,16,'26'),(7,4,'27'),(7,17,'21'),(8,18,'30'),(9,17,'24'),(10,16,'23'),(10,21,'30 e lode'),(11,23,'26'),(12,24,'27'),(13,25,'24'),(14,15,'30'),(14,16,'27'),(15,17,'25'),(16,18,'22'),(17,19,'28'),(18,19,'28'),(19,5,'22'),(19,13,'28'),(19,18,'26'),(20,8,'30'),(20,31,'30 e lode'),(21,23,'18'),(22,23,'Rimandato'),(23,23,'Rimandato'),(24,8,'30'),(24,31,'30 e lode'),(25,23,'23'),(26,23,'18'),(27,8,'Rimandato'),(28,23,'Rimandato'),(29,23,'Rimandato');
/*!40000 ALTER TABLE `DettaglioVerbale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Docenti`
--

DROP TABLE IF EXISTS `Docenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Docenti` (
                           `id_utente` int NOT NULL,
                           `nome` varchar(50) DEFAULT NULL,
                           `cognome` varchar(50) DEFAULT NULL,
                           PRIMARY KEY (`id_utente`),
                           CONSTRAINT `Docenti_ibfk_1` FOREIGN KEY (`id_utente`) REFERENCES `Utenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Docenti`
--

LOCK TABLES `Docenti` WRITE;
/*!40000 ALTER TABLE `Docenti` DISABLE KEYS */;
INSERT INTO `Docenti` VALUES (1,'Mario','Rossi'),(2,'Laura','Bianchi'),(9,'Paolo','Rosa'),(10,'Chiara','Azzurra'),(26,'Martina','Porpora'),(27,'Claudio','Seppia'),(28,'Renata','Sabbia'),(29,'Alberto','Corallo'),(30,'Barbara','Ambra');
/*!40000 ALTER TABLE `Docenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Iscrizioni`
--

DROP TABLE IF EXISTS `Iscrizioni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Iscrizioni` (
                              `id_studente` int NOT NULL,
                              `id_appello` int NOT NULL,
                              `voto` enum('','Assente','Rimandato','Riprovato','18','19','20','21','22','23','24','25','26','27','28','29','30','30 e lode') DEFAULT '',
                              `stato_valutazione` enum('Non inserito','Inserito','Pubblicato','Rifiutato','Verbalizzato') NOT NULL DEFAULT 'Non inserito',
                              PRIMARY KEY (`id_studente`,`id_appello`),
                              KEY `id_appello` (`id_appello`),
                              CONSTRAINT `Iscrizioni_ibfk_1` FOREIGN KEY (`id_studente`) REFERENCES `Studenti` (`id_utente`) ON DELETE CASCADE,
                              CONSTRAINT `Iscrizioni_ibfk_2` FOREIGN KEY (`id_appello`) REFERENCES `Appelli` (`id_appello`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Iscrizioni`
--

LOCK TABLES `Iscrizioni` WRITE;
/*!40000 ALTER TABLE `Iscrizioni` DISABLE KEYS */;
INSERT INTO `Iscrizioni` VALUES (3,1,'30 e lode','Inserito'),(3,2,'Riprovato','Verbalizzato'),(3,4,'30','Verbalizzato'),(3,16,'28','Inserito'),(3,19,'23','Inserito'),(3,28,'','Non inserito'),(4,1,'22','Verbalizzato'),(4,2,'Rimandato','Inserito'),(4,5,'26','Verbalizzato'),(4,7,'27','Verbalizzato'),(4,28,'','Non inserito'),(5,1,'19','Verbalizzato'),(5,3,'Assente','Inserito'),(5,6,'24','Verbalizzato'),(5,8,'Rimandato','Pubblicato'),(5,9,'22','Verbalizzato'),(5,17,'','Non inserito'),(5,18,'Assente','Inserito'),(5,19,'','Non inserito'),(6,1,'28','Verbalizzato'),(6,3,'22','Verbalizzato'),(6,18,'19','Inserito'),(7,2,'25','Verbalizzato'),(7,4,'Assente','Pubblicato'),(7,7,'18','Inserito'),(7,10,'Riprovato','Pubblicato'),(7,14,'Assente','Inserito'),(7,16,'Assente','Pubblicato'),(7,18,'Rimandato','Inserito'),(7,28,'','Non inserito'),(8,3,'27','Verbalizzato'),(8,5,'23','Verbalizzato'),(8,6,'Rimandato','Verbalizzato'),(8,11,'29','Verbalizzato'),(8,12,'30','Verbalizzato'),(8,19,'21','Inserito'),(11,1,'26','Verbalizzato'),(11,16,'30','Pubblicato'),(11,28,'','Non inserito'),(12,2,'27','Verbalizzato'),(12,7,'','Non inserito'),(12,20,'24','Inserito'),(13,3,'19','Verbalizzato'),(13,7,'','Non inserito'),(13,8,'27','Rifiutato'),(13,9,'28','Verbalizzato'),(13,17,'','Non inserito'),(13,19,'27','Inserito'),(14,4,'28','Verbalizzato'),(14,18,'22','Inserito'),(15,5,'22','Verbalizzato'),(15,16,'','Non inserito'),(15,20,'','Non inserito'),(15,21,'30','Verbalizzato'),(16,6,'26','Verbalizzato'),(16,11,'23','Verbalizzato'),(16,16,'','Non inserito'),(16,20,'','Non inserito'),(16,21,'27','Verbalizzato'),(16,28,'','Non inserito'),(17,7,'21','Verbalizzato'),(17,10,'24','Verbalizzato'),(17,22,'25','Verbalizzato'),(18,8,'30','Verbalizzato'),(18,9,'26','Verbalizzato'),(18,17,'','Non inserito'),(18,23,'22','Verbalizzato'),(19,8,'','Non inserito'),(19,17,'','Non inserito'),(19,18,'25','Inserito'),(19,24,'28','Verbalizzato'),(19,25,'28','Verbalizzato'),(20,8,'','Non inserito'),(20,19,'','Non inserito'),(20,20,'23','Inserito'),(21,5,'26','Verbalizzato'),(21,11,'30 e lode','Verbalizzato'),(21,20,'','Non inserito'),(22,4,'25','Verbalizzato'),(23,3,'28','Verbalizzato'),(23,13,'Rimandato','Pubblicato'),(23,17,'','Non inserito'),(24,2,'30','Verbalizzato'),(24,14,'27','Verbalizzato'),(24,28,'','Non inserito'),(25,1,'27','Verbalizzato'),(25,7,'21','Pubblicato'),(25,15,'24','Verbalizzato'),(25,28,'','Non inserito'),(31,12,'30 e lode','Verbalizzato'),(31,14,'23','Non inserito'),(31,28,'','Non inserito');
/*!40000 ALTER TABLE `Iscrizioni` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `IscrizioniCorsi`
--

DROP TABLE IF EXISTS `IscrizioniCorsi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `IscrizioniCorsi` (
                                   `id_studente` int NOT NULL,
                                   `id_corso` int NOT NULL,
                                   `anno_accademico` varchar(9) NOT NULL,
                                   `data_iscrizione` date NOT NULL,
                                   PRIMARY KEY (`id_studente`,`id_corso`,`anno_accademico`),
                                   KEY `id_corso` (`id_corso`),
                                   CONSTRAINT `IscrizioniCorsi_ibfk_1` FOREIGN KEY (`id_studente`) REFERENCES `Studenti` (`id_utente`) ON DELETE CASCADE,
                                   CONSTRAINT `IscrizioniCorsi_ibfk_2` FOREIGN KEY (`id_corso`) REFERENCES `Corsi` (`id_corso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `IscrizioniCorsi`
--

LOCK TABLES `IscrizioniCorsi` WRITE;
/*!40000 ALTER TABLE `IscrizioniCorsi` DISABLE KEYS */;
INSERT INTO `IscrizioniCorsi` VALUES (3,1,'2024/2025','2024-09-15'),(3,3,'2024/2025','2024-09-15'),(3,15,'2024/2025','2024-09-10'),(4,1,'2024/2025','2024-09-15'),(4,4,'2024/2025','2024-09-15'),(5,1,'2024/2025','2024-09-15'),(5,2,'2024/2025','2024-09-15'),(5,5,'2024/2025','2024-09-15'),(6,1,'2024/2025','2024-09-15'),(6,2,'2024/2025','2024-09-15'),(7,1,'2024/2025','2024-09-15'),(7,3,'2024/2025','2024-09-15'),(8,2,'2024/2025','2024-09-15'),(8,4,'2024/2025','2024-09-15'),(8,5,'2024/2025','2024-09-15'),(11,1,'2024/2025','2024-09-15'),(11,14,'2024/2025','2024-09-17'),(12,1,'2024/2025','2024-09-15'),(12,13,'2024/2025','2024-09-18'),(13,2,'2024/2025','2024-09-15'),(13,12,'2024/2025','2024-09-19'),(14,3,'2024/2025','2024-09-15'),(14,11,'2024/2025','2024-09-10'),(15,4,'2024/2025','2024-09-15'),(15,14,'2024/2025','2024-09-15'),(16,4,'2024/2025','2024-09-15'),(16,5,'2024/2025','2024-09-15'),(16,14,'2024/2025','2024-09-15'),(17,1,'2024/2025','2024-09-15'),(17,3,'2024/2025','2024-09-15'),(17,15,'2024/2025','2024-09-15'),(18,2,'2024/2025','2024-09-15'),(18,16,'2024/2025','2024-09-15'),(19,17,'2024/2025','2024-09-15'),(19,18,'2024/2025','2024-09-15'),(21,4,'2024/2025','2024-09-15'),(22,3,'2024/2025','2024-09-15'),(23,2,'2024/2025','2024-09-15'),(23,6,'2024/2025','2024-09-15'),(24,1,'2024/2025','2024-09-15'),(24,7,'2024/2025','2024-09-15'),(25,1,'2024/2025','2024-09-15'),(25,8,'2024/2025','2024-09-15'),(31,5,'2024/2025','2024-09-20');
/*!40000 ALTER TABLE `IscrizioniCorsi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Studenti`
--

DROP TABLE IF EXISTS `Studenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Studenti` (
                            `id_utente` int NOT NULL,
                            `matricola` varchar(20) NOT NULL,
                            `nome` varchar(50) DEFAULT NULL,
                            `cognome` varchar(50) DEFAULT NULL,
                            `corso_di_laurea` varchar(100) DEFAULT NULL,
                            PRIMARY KEY (`id_utente`),
                            UNIQUE KEY `matricola` (`matricola`),
                            CONSTRAINT `Studenti_ibfk_1` FOREIGN KEY (`id_utente`) REFERENCES `Utenti` (`id_utente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Studenti`
--

LOCK TABLES `Studenti` WRITE;
/*!40000 ALTER TABLE `Studenti` DISABLE KEYS */;
INSERT INTO `Studenti` VALUES (3,'S1001','Anna','Verdi','Informatica'),(4,'S1002','Carlo','Neri','Ingegneria'),(5,'S1003','Dario','Gialli','Matematica'),(6,'S1004','Marco','Blu','Fisica'),(7,'S1005','Sara','Viola','Chimica'),(8,'S1006','Luca','Arancio','Informatica'),(11,'S1007','Elena','Marrone','Informatica'),(12,'S1008','Fabio','Celeste','Ingegneria'),(13,'S1009','Giulia','Beige','Matematica'),(14,'S1010','Roberto','Indaco','Fisica'),(15,'S1011','Alessia','Carminio','Chimica'),(16,'S1012','Davide','Magenta','Informatica'),(17,'S1013','Monica','Lime','Ingegneria'),(18,'S1014','Stefano','Cobalto','Matematica'),(19,'S1015','Claudia','Bordeaux','Fisica'),(20,'S1016','Antonio','Acquamarina','Chimica'),(21,'S1017','Valentina','Ocra','Informatica'),(22,'S1018','Francesco','Lilla','Ingegneria'),(23,'S1019','Greta','Smeraldo','Matematica'),(24,'S1020','Leonardo','Granata','Fisica'),(25,'S1021','Silvia','Turchese','Chimica'),(31,'S1022','Matteo','Grigio','Informatica');
/*!40000 ALTER TABLE `Studenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Utenti`
--

DROP TABLE IF EXISTS `Utenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Utenti` (
                          `id_utente` int NOT NULL AUTO_INCREMENT,
                          `email` varchar(100) NOT NULL,
                          `password` varchar(100) NOT NULL,
                          `ruolo` enum('studente','docente') NOT NULL,
                          PRIMARY KEY (`id_utente`),
                          UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Utenti`
--

LOCK TABLES `Utenti` WRITE;
/*!40000 ALTER TABLE `Utenti` DISABLE KEYS */;
INSERT INTO `Utenti` VALUES (1,'mario.rossi@uni.it','password1','docente'),(2,'laura.bianchi@uni.it','password2','docente'),(3,'anna.verdi@studenti.uni.it','password3','studente'),(4,'carlo.neri@studenti.uni.it','password4','studente'),(5,'dario.gialli@studenti.uni.it','password5','studente'),(6,'marco.blu@studenti.uni.it','password6','studente'),(7,'sara.viola@studenti.uni.it','password7','studente'),(8,'luca.arancio@studenti.uni.it','password8','studente'),(9,'paolo.rosa@uni.it','password9','docente'),(10,'chiara.azzurra@uni.it','password10','docente'),(11,'elena.marrone@studenti.uni.it','password11','studente'),(12,'fabio.celeste@studenti.uni.it','password12','studente'),(13,'giulia.beige@studenti.uni.it','password13','studente'),(14,'roberto.indaco@studenti.uni.it','password14','studente'),(15,'alessia.carminio@studenti.uni.it','password15','studente'),(16,'davide.magenta@studenti.uni.it','password16','studente'),(17,'monica.lime@studenti.uni.it','password17','studente'),(18,'stefano.cobalto@studenti.uni.it','password18','studente'),(19,'claudia.bordeaux@studenti.uni.it','password19','studente'),(20,'antonio.acquamarina@studenti.uni.it','password20','studente'),(21,'valentina.ocra@studenti.uni.it','password21','studente'),(22,'francesco.lilla@studenti.uni.it','password22','studente'),(23,'ish993956@gmail.com','password23','studente'),(24,'leonardo.granata@studenti.uni.it','password24','studente'),(25,'silvia.turchese@studenti.uni.it','password25','studente'),(26,'martina.porpora@uni.it','password26','docente'),(27,'claudio.seppia@uni.it','password27','docente'),(28,'renata.sabbia@uni.it','password28','docente'),(29,'alberto.corallo@uni.it','password29','docente'),(30,'barbara.ambra@uni.it','password30','docente'),(31,'matteo.grigio@studenti.uni.it','password31','studente');
/*!40000 ALTER TABLE `Utenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Verbali`
--

DROP TABLE IF EXISTS `Verbali`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Verbali` (
                           `id_verbale` int NOT NULL AUTO_INCREMENT,
                           `codice_verbale` varchar(50) NOT NULL,
                           `id_appello` int NOT NULL,
                           `data_creazione` datetime NOT NULL,
                           PRIMARY KEY (`id_verbale`),
                           UNIQUE KEY `codice_verbale` (`codice_verbale`),
                           KEY `id_appello` (`id_appello`),
                           CONSTRAINT `Verbali_ibfk_1` FOREIGN KEY (`id_appello`) REFERENCES `Appelli` (`id_appello`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Verbali`
--

LOCK TABLES `Verbali` WRITE;
/*!40000 ALTER TABLE `Verbali` DISABLE KEYS */;
INSERT INTO `Verbali` VALUES (1,'VRB-ALG-20250210-001',1,'2025-02-10 14:30:00'),(2,'VRB-ALG-20250305-001',2,'2025-03-05 10:15:00'),(3,'VRB-ANM-20250301-001',3,'2025-03-01 11:00:00'),(4,'VRB-FIS-20250310-001',4,'2025-03-10 09:45:00'),(5,'VRB-CHI-20250401-001',5,'2025-04-01 15:30:00'),(6,'VRB-BDD-20250420-001',6,'2025-04-20 16:00:00'),(7,'VRB-ALG-20250525-001',7,'2025-05-25 13:15:00'),(8,'VRB-ANM-20250620-001',8,'2025-06-20 10:30:00'),(9,'VRB-FIS-20250715-001',10,'2025-07-15 14:00:00'),(10,'VRB-CHI-20250720-001',11,'2025-07-20 11:45:00'),(11,'VRB-PRG-20250730-001',13,'2025-07-30 15:30:00'),(12,'VRB-SIS-20250810-001',14,'2025-08-10 09:00:00'),(13,'VRB-RET-20250815-001',15,'2025-08-15 14:30:00'),(14,'VRB-BIO-20250920-001',21,'2025-09-20 10:45:00'),(15,'VRB-STA-20250925-001',22,'2025-09-25 16:15:00'),(16,'VRB-INT-20251010-001',23,'2025-10-10 13:30:00'),(17,'VRB-MAC-20251020-001',24,'2025-10-20 11:30:00'),(18,'VRB-ELE-20251025-001',25,'2025-10-25 15:45:00'),(19,'VRB-ANM-20250710-001',9,'2025-07-10 09:30:00'),(20,'VRB-BDD-20250725-001',12,'2025-07-25 14:00:00'),(21,'VER-13-1747774090721',13,'2025-05-20 22:48:11'),(22,'VER-13-1747774699595',13,'2025-05-20 22:58:20'),(23,'VER-13-1747775356652',13,'2025-05-20 23:09:17'),(24,'VER-12-1747775587794',12,'2025-05-20 23:13:08'),(25,'VER-13-1747903051830',13,'2025-05-22 10:37:32'),(26,'VER-13-1747931863886',13,'2025-05-22 18:37:44'),(27,'VER-6-1747994307355',6,'2025-05-23 11:58:27'),(28,'VER-13-1748183718693',13,'2025-05-25 16:35:19'),(29,'VER-13-1748183987576',13,'2025-05-25 16:39:48');
/*!40000 ALTER TABLE `Verbali` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-25 16:49:01
