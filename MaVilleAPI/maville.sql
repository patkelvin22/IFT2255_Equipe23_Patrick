-- MySQL dump 10.13  Distrib 9.0.1, for macos15.1 (arm64)
--
-- Host: localhost    Database: maville
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Boroughs`
--

DROP TABLE IF EXISTS `Boroughs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Boroughs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Boroughs`
--

LOCK TABLES `Boroughs` WRITE;
/*!40000 ALTER TABLE `Boroughs` DISABLE KEYS */;
INSERT INTO `Boroughs` VALUES (1,'Pointe-aux-Trembles'),(2,'Montréal-Est'),(3,'Rivière-des-Prairies'),(4,'Montréal-Nord'),(5,'Anjou'),(6,'Mercier-Est'),(7,'Mercier-Ouest'),(8,'St-Léonard'),(9,'Rosemont'),(10,'Hochelaga-Maisonneuve'),(11,'Vieux-Rosemont'),(12,'Petite-Patrie'),(13,'St-Michel'),(14,'Ahuntsic'),(15,'Villeray'),(16,'La Petite-Patrie'),(17,'Le Plateau-Mont-Royal'),(18,'Centre-Sud'),(19,'Parc-Extension'),(20,'Outremont'),(21,'Quartier des Spectacles'),(22,'Vieux-Montréal'),(23,'Quartier International'),(24,'Centre-ville'),(25,'Griffintown'),(26,'Île-des-Sœurs'),(27,'Shaughnessy Village'),(28,'Petite-Bourgogne'),(29,'Pointe-Saint-Charles'),(30,'Nouveau-Bordeaux'),(31,'Ville Mont-Royal'),(32,'Côte-des-Neiges'),(33,'Snowdon'),(34,'Westmount'),(35,'Notre-Dame-de-Grâce'),(36,'St-Henri'),(37,'Ville-Émard'),(38,'Verdun'),(39,'Cartierville'),(40,'St-Laurent'),(41,'Côte-Saint-Luc'),(42,'Montréal-Ouest'),(43,'Aéroport de Montréal');
/*!40000 ALTER TABLE `Boroughs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FSAs`
--

DROP TABLE IF EXISTS `FSAs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FSAs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fsa` char(3) NOT NULL,
  `borough_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsa` (`fsa`),
  KEY `fk_borough_fsa` (`borough_id`),
  CONSTRAINT `fk_borough_fsa` FOREIGN KEY (`borough_id`) REFERENCES `Boroughs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FSAs`
--

LOCK TABLES `FSAs` WRITE;
/*!40000 ALTER TABLE `FSAs` DISABLE KEYS */;
INSERT INTO `FSAs` VALUES (1,'H1A',1),(2,'H1B',2),(3,'H1C',3),(4,'H1E',3),(5,'H1G',4),(6,'H1H',4),(7,'H1J',5),(8,'H1K',5),(9,'H1L',6),(10,'H1M',7),(11,'H1N',7),(12,'H1P',8),(13,'H1R',8),(14,'H1S',8),(15,'H1T',9),(16,'H1V',9),(17,'H1W',10),(18,'H1X',11),(19,'H1Y',12),(20,'H1Z',13),(21,'H2A',13),(22,'H2B',14),(23,'H2C',14),(24,'H2E',15),(25,'H2G',16),(26,'H2H',17),(27,'H2J',17),(28,'H2K',18),(29,'H2L',18),(30,'H2M',14),(31,'H2N',19),(32,'H2P',15),(33,'H2R',15),(34,'H2S',16),(35,'H2T',17),(36,'H2V',20),(37,'H2W',17),(38,'H2X',21),(39,'H2Y',22),(40,'H2Z',23),(41,'H3A',24),(42,'H3B',24),(43,'H3C',25),(44,'H3E',26),(45,'H3G',24),(46,'H3H',27),(47,'H3J',28),(48,'H3K',29),(49,'H3L',14),(50,'H3M',30),(51,'H3N',19),(52,'H3P',31),(53,'H3R',31),(54,'H3S',32),(55,'H3T',32),(56,'H3V',32),(57,'H3W',32),(58,'H3X',33),(59,'H3Y',34),(60,'H3Z',34),(61,'H4A',35),(62,'H4B',35),(63,'H4C',36),(64,'H4E',37),(65,'H4G',38),(66,'H4H',38),(67,'H4J',39),(68,'H4K',39),(69,'H4L',40),(70,'H4M',40),(71,'H4N',40),(72,'H4P',32),(73,'H4R',40),(74,'H4S',40),(75,'H4T',40),(76,'H4V',35),(77,'H4W',41),(78,'H4X',42),(79,'H4Y',43),(80,'H4Z',24);
/*!40000 ALTER TABLE `FSAs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Intervenants`
--

DROP TABLE IF EXISTS `Intervenants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Intervenants` (
  `_id` int NOT NULL AUTO_INCREMENT,
  `id` varchar(8) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `organisation_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `submittercategory` varchar(255) NOT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Intervenants`
--

LOCK TABLES `Intervenants` WRITE;
/*!40000 ALTER TABLE `Intervenants` DISABLE KEYS */;
INSERT INTO `Intervenants` VALUES (1,'','Intervenant 1','Travaux Urbains MTL','int1@example.com','hash1','Entreprise publique'),(2,'','Intervenant 2','Construction Inc.','int2@example.com','hash2','Indépendant'),(3,'','Intervenant 3','MTL Toitures','int3@example.com','hash3','Entreprise publique'),(4,'','Intervenant 4','Éclairage Ville','int4@example.com','hash4','Entreprise publique'),(5,'','Intervenant 5','Projets Vert','int5@example.com','hash5','Organisation Non-Profit');
/*!40000 ALTER TABLE `Intervenants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Notifications`
--

DROP TABLE IF EXISTS `Notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `message` text NOT NULL,
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_notification_resident` (`resident_id`),
  CONSTRAINT `fk_notification_resident` FOREIGN KEY (`resident_id`) REFERENCES `Residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Notifications`
--

LOCK TABLES `Notifications` WRITE;
/*!40000 ALTER TABLE `Notifications` DISABLE KEYS */;
INSERT INTO `Notifications` VALUES (1,4,'Votre requête de travail a reçu une candidature.','2024-12-05 23:59:14',0),(2,5,'Votre requête de travail a reçu une candidature.','2024-12-05 23:59:14',0),(3,5,'Votre requête de travail a reçu une deuxième candidature.','2024-12-05 23:59:14',0),(4,1,'Un nouveau projet a été soumis dans votre quartier : Réparation de trottoirs.','2024-12-05 23:59:14',0),(5,2,'Un nouveau projet a été soumis dans votre quartier : Installation de lampadaires.','2024-12-05 23:59:14',0),(6,3,'Un nouveau projet a été soumis dans votre quartier : Réaménagement de parc.','2024-12-05 23:59:14',0);
/*!40000 ALTER TABLE `Notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Residents`
--

DROP TABLE IF EXISTS `Residents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Residents` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(255) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `address` text NOT NULL,
  `birth_date` date NOT NULL,
  `postal_code` varchar(10) NOT NULL,
  `borough_id` int NOT NULL,
  `password_hash` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_borough` (`borough_id`),
  CONSTRAINT `fk_borough` FOREIGN KEY (`borough_id`) REFERENCES `Boroughs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Residents`
--

LOCK TABLES `Residents` WRITE;
/*!40000 ALTER TABLE `Residents` DISABLE KEYS */;
INSERT INTO `Residents` VALUES (1,'Alice Dupont','5141234567','123 Rue des Fleurs','1990-05-15','H2X1Y5',1,''),(2,'Jean Tremblay','5149876543','456 Boulevard Saint-Laurent','1985-11-25','H2X3Z7',1,''),(3,'Marie Curie','4381230987','789 Avenue des Pins','1992-03-10','H3A1B2',2,''),(4,'Paul Rivière','5147654321','12 Rue Sherbrooke','1988-07-20','H3C2J5',3,''),(5,'Sophie Trudeau','4380987654','34 Boulevard René-Lévesque','1995-09-05','H3B1L6',3,'');
/*!40000 ALTER TABLE `Residents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkProjects`
--

DROP TABLE IF EXISTS `WorkProjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkProjects` (
  `_id` int NOT NULL AUTO_INCREMENT,
  `id` varchar(255) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('Pending','Approved','In Progress','Completed') NOT NULL DEFAULT 'Pending',
  `created_by` int NOT NULL,
  `borough_id` int NOT NULL,
  `reasonCategory` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_intervenant` (`created_by`),
  KEY `fk_borough_project` (`borough_id`),
  CONSTRAINT `fk_borough_project` FOREIGN KEY (`borough_id`) REFERENCES `Boroughs` (`id`),
  CONSTRAINT `fk_intervenant` FOREIGN KEY (`created_by`) REFERENCES `Intervenants` (`_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkProjects`
--

LOCK TABLES `WorkProjects` WRITE;
/*!40000 ALTER TABLE `WorkProjects` DISABLE KEYS */;
INSERT INTO `WorkProjects` VALUES (1,NULL,'2024-12-01','2024-12-15','Pending',1,1,'Réparation de trottoirs'),(2,NULL,'2024-12-05','2024-12-20','Pending',2,2,'Installation de lampadaires'),(3,NULL,'2025-01-10','2025-02-15','Approved',3,3,'Réaménagement de parc'),(4,NULL,'2025-03-01','2025-04-01','Pending',4,1,'Entretien de routes'),(5,NULL,'2025-01-15','2025-02-01','In Progress',5,3,'Nettoyage des caniveaux');
/*!40000 ALTER TABLE `WorkProjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkRequestCandidates`
--

DROP TABLE IF EXISTS `WorkRequestCandidates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkRequestCandidates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `work_request_id` int NOT NULL,
  `intervenant_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_work_request_candidates` (`work_request_id`),
  KEY `fk_intervenant_candidates` (`intervenant_id`),
  CONSTRAINT `fk_intervenant_candidates` FOREIGN KEY (`intervenant_id`) REFERENCES `Intervenants` (`_id`),
  CONSTRAINT `fk_work_request_candidates` FOREIGN KEY (`work_request_id`) REFERENCES `WorkRequests` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkRequestCandidates`
--

LOCK TABLES `WorkRequestCandidates` WRITE;
/*!40000 ALTER TABLE `WorkRequestCandidates` DISABLE KEYS */;
INSERT INTO `WorkRequestCandidates` VALUES (5,4,5),(6,5,1),(7,5,3);
/*!40000 ALTER TABLE `WorkRequestCandidates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkRequests`
--

DROP TABLE IF EXISTS `WorkRequests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkRequests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resident_id` int NOT NULL,
  `project_id` int NOT NULL,
  `request_date` date NOT NULL,
  `status` enum('Open','Assigned','Closed') NOT NULL DEFAULT 'Open',
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `work_type` enum('Toiture','Fenêtres','Portes','Revêtement extérieur','Piscine','Patio','Balcon','Clôture','Gazon','Pavé uni','Asphalte') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_resident` (`resident_id`),
  KEY `fk_project` (`project_id`),
  CONSTRAINT `fk_project` FOREIGN KEY (`project_id`) REFERENCES `WorkProjects` (`_id`),
  CONSTRAINT `fk_resident` FOREIGN KEY (`resident_id`) REFERENCES `Residents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkRequests`
--

LOCK TABLES `WorkRequests` WRITE;
/*!40000 ALTER TABLE `WorkRequests` DISABLE KEYS */;
INSERT INTO `WorkRequests` VALUES (1,1,1,'2024-12-01','Open','Réparation toiture','Réparer les fuites sur le toit principal.','Toiture'),(2,2,2,'2024-12-03','Open','Installation nouvelles fenêtres','Installer des fenêtres écoénergétiques.','Fenêtres'),(3,3,3,'2024-12-05','Open','Réparation porte d’entrée','Réparer ou remplacer la porte d’entrée.','Portes'),(4,4,4,'2024-12-07','Open','Amélioration du patio','Ajouter un patio en bois traité.','Patio'),(5,5,5,'2024-12-10','Open','Installation clôture','Installer une clôture autour du jardin.','Clôture');
/*!40000 ALTER TABLE `WorkRequests` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-06  2:05:46
