-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: master_timesheet
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `activity_type` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `other_note` text,
  `user_id` int DEFAULT NULL,
  `user_details_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr8wdbwcm475hs03x515cjlftv` (`user_id`),
  KEY `FKpxfj690appbvdf0jlfhwgx43q` (`user_details_id`),
  CONSTRAINT `FKpxfj690appbvdf0jlfhwgx43q` FOREIGN KEY (`user_details_id`) REFERENCES `user_detail` (`user_detail_id`),
  CONSTRAINT `FKr8wdbwcm475hs03x515cjlftv` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (1,'2025-06-18 20:01:18.000000','2025-06-18 20:01:18.000000','ADD_USER','0:0:0:0:0:0:0:1','Add user',NULL,1,NULL),(2,'2025-06-18 21:22:20.000000','2025-06-18 21:22:20.000000','ADD_USER','0:0:0:0:0:0:0:1','Add user',NULL,1,NULL),(3,'2025-06-18 21:23:07.000000','2025-06-18 21:23:07.000000','ADD_USER','0:0:0:0:0:0:0:1','Add user',NULL,1,NULL),(4,'2025-06-18 21:35:12.000000','2025-06-18 21:35:12.000000','SET_USER_DETAILS','0:0:0:0:0:0:0:1','Add user details',NULL,1,1),(5,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000','SUBMIT_TIMESHEET','0:0:0:0:0:0:0:1','Time sheet submit',NULL,1,1),(6,'2025-06-19 22:42:44.000000','2025-06-19 22:42:44.000000','SUBMIT_TIMESHEET','0:0:0:0:0:0:0:1','Time sheet submit',NULL,1,1),(7,'2025-06-19 22:46:16.000000','2025-06-19 22:46:16.000000','SET_USER_DETAILS','0:0:0:0:0:0:0:1','Add user details',NULL,1,2);
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `clientName` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  `zipCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `client_chk_1` CHECK ((`type` between 0 and 3))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (1,'2025-06-18 20:01:49.000000','2025-06-18 20:01:49.000000','','Akash','+23123','',0,'123123'),(2,'2025-06-18 20:02:07.000000','2025-06-18 20:02:07.000000','','A vendor','+12312312','',3,'22222'),(3,'2025-06-18 21:24:03.000000','2025-06-18 21:24:03.000000','','Elcertical board company ','111222333','test',0,'111111'),(4,'2025-06-18 21:24:27.000000','2025-06-18 21:24:27.000000','','Vendor 5','1112222333','',3,'999999'),(5,'2025-06-18 21:27:25.000000','2025-06-18 21:27:25.000000','','Third party employer','11122222','',2,'111111');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `db_name` varchar(255) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `file_folder` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `timesheet_submit_email` varchar(255) DEFAULT NULL,
  `url_slug` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `varify` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'2025-06-18 13:30:48.000000','2025-06-18 13:30:48.000000',_binary '','','master_timesheet','','expo_freight',NULL,'EXPO-FREIGHT','shubratodn44985@gmail.com','expo-freight','2902094b-6e87-445d-b4d7-037bd6fa850b',_binary ''),(2,'2025-06-18 13:51:41.000000','2025-06-18 13:51:41.000000',_binary '','','master_timesheet','','master_timesheet',NULL,'master_timesheet','mycomputer44985@gmail.com','master_timesheet','aa6138c5-340f-41f9-84a2-96813921897f',_binary '');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hour_log`
--

DROP TABLE IF EXISTS `hour_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hour_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `daily_hours` float DEFAULT NULL,
  `extra_hours` float DEFAULT NULL,
  `hourlog_status` tinyint DEFAULT NULL,
  `hours_date` date DEFAULT NULL,
  `notes` text,
  `reject_reason` text,
  `vacation_hours` float DEFAULT NULL,
  `user_detail_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1mg4k657pdlsvkplyhkh02xj6` (`user_detail_id`),
  CONSTRAINT `FK1mg4k657pdlsvkplyhkh02xj6` FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`),
  CONSTRAINT `hour_log_chk_1` CHECK ((`hourlog_status` between 0 and 3))
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hour_log`
--

LOCK TABLES `hour_log` WRITE;
/*!40000 ALTER TABLE `hour_log` DISABLE KEYS */;
INSERT INTO `hour_log` VALUES (1,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-01',NULL,NULL,0,1),(2,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-02',NULL,NULL,0,1),(3,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-03',NULL,NULL,0,1),(4,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-04',NULL,NULL,0,1),(5,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-05',NULL,NULL,0,1),(6,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-06',NULL,NULL,0,1),(7,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-07',NULL,NULL,0,1),(8,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-08',NULL,NULL,0,1),(9,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-09',NULL,NULL,0,1),(10,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',0,0,3,'2024-06-10',NULL,NULL,0,1),(11,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-11',NULL,NULL,0,1),(12,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-12',NULL,NULL,0,1),(13,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-13',NULL,NULL,0,1),(14,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-14',NULL,NULL,0,1),(15,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-15',NULL,NULL,0,1),(16,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-16',NULL,NULL,0,1),(17,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-17',NULL,NULL,0,1),(18,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-18',NULL,NULL,0,1),(19,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-19',NULL,NULL,0,1),(20,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-20',NULL,NULL,0,1),(21,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-21',NULL,NULL,0,1),(22,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-22',NULL,NULL,0,1),(23,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-23',NULL,NULL,0,1),(24,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-24',NULL,NULL,0,1),(25,'2025-06-18 21:39:43.000000','2025-06-18 21:39:43.000000',0,0,3,'2024-06-25',NULL,NULL,0,1),(26,'2025-06-18 21:39:44.000000','2025-06-18 21:39:44.000000',0,0,3,'2024-06-26',NULL,NULL,0,1),(27,'2025-06-18 21:39:44.000000','2025-06-18 21:39:44.000000',0,0,3,'2024-06-27',NULL,NULL,0,1),(28,'2025-06-18 21:39:44.000000','2025-06-18 21:39:44.000000',0,0,3,'2024-06-28',NULL,NULL,0,1),(29,'2025-06-18 21:39:44.000000','2025-06-18 21:39:44.000000',0,0,3,'2024-06-29',NULL,NULL,0,1),(30,'2025-06-18 21:39:44.000000','2025-06-18 21:39:44.000000',0,0,3,'2024-06-30',NULL,NULL,0,1),(31,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-01',NULL,NULL,0,1),(32,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-02',NULL,NULL,0,1),(33,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-03',NULL,NULL,0,1),(34,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-04',NULL,NULL,0,1),(35,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-05',NULL,NULL,0,1),(36,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-06',NULL,NULL,0,1),(37,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-07',NULL,NULL,0,1),(38,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-08',NULL,NULL,0,1),(39,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-09',NULL,NULL,0,1),(40,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-10',NULL,NULL,0,1),(41,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-11',NULL,NULL,0,1),(42,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-12',NULL,NULL,0,1),(43,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-13',NULL,NULL,0,1),(44,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-14',NULL,NULL,0,1),(45,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-15',NULL,NULL,0,1),(46,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-16',NULL,NULL,0,1),(47,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-17',NULL,NULL,0,1),(48,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-18',NULL,NULL,0,1),(49,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-19',NULL,NULL,0,1),(50,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-20',NULL,NULL,0,1),(51,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-21',NULL,NULL,0,1),(52,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-22',NULL,NULL,0,1),(53,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-23',NULL,NULL,0,1),(54,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-24',NULL,NULL,0,1),(55,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-25',NULL,NULL,0,1),(56,'2025-06-19 22:42:45.000000','2025-06-19 22:42:45.000000',0,0,3,'2025-06-26',NULL,NULL,0,1),(57,'2025-06-19 22:42:46.000000','2025-06-19 22:42:46.000000',0,0,3,'2025-06-27',NULL,NULL,0,1),(58,'2025-06-19 22:42:46.000000','2025-06-19 22:42:46.000000',0,0,3,'2025-06-28',NULL,NULL,0,1),(59,'2025-06-19 22:42:46.000000','2025-06-19 22:42:46.000000',0,0,3,'2025-06-29',NULL,NULL,0,1),(60,'2025-06-19 22:42:46.000000','2025-06-19 22:42:46.000000',0,0,3,'2025-06-30',NULL,NULL,0,1);
/*!40000 ALTER TABLE `hour_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hour_log_file`
--

DROP TABLE IF EXISTS `hour_log_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hour_log_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `approve` bit(1) DEFAULT NULL,
  `approved_date` datetime(6) DEFAULT NULL,
  `description` text,
  `end_date` date DEFAULT NULL,
  `file_original_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `is_hour_added` bit(1) DEFAULT NULL,
  `reject` bit(1) DEFAULT NULL,
  `reject_reason` text,
  `rejected_date` datetime(6) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `approved_by` int DEFAULT NULL,
  `rejected_by` int DEFAULT NULL,
  `user_detail_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2lrhpdliwl7j9wd5ar4yq3fq6` (`approved_by`),
  KEY `FK449magr22eke1a14lysjrdv91` (`rejected_by`),
  KEY `FKqdnuajhylhwj13n1y6eapw4j` (`user_detail_id`),
  CONSTRAINT `FK2lrhpdliwl7j9wd5ar4yq3fq6` FOREIGN KEY (`approved_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK449magr22eke1a14lysjrdv91` FOREIGN KEY (`rejected_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKqdnuajhylhwj13n1y6eapw4j` FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hour_log_file`
--

LOCK TABLES `hour_log_file` WRITE;
/*!40000 ALTER TABLE `hour_log_file` DISABLE KEYS */;
INSERT INTO `hour_log_file` VALUES (1,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',_binary '\0',NULL,'Hello, <br><br>Timesheet is submit for client : Elcertical board company <br><br>for time period:06/01/2024 To 06/30/2024<br><br>Please check and approve. <br><br>Thank you.','2024-06-30','/User/shubratodn44985u2/Timesheet/elcertical-board-company/17502611814870Timesheet1.png','Screenshot 2024-01-24 182346.png',_binary '\0',_binary '\0',NULL,NULL,'','2024-06-01',NULL,NULL,1),(2,'2025-06-19 22:42:44.000000','2025-06-19 22:42:44.000000',_binary '\0',NULL,'Hello, <br><br>Timesheet is submit for client : Elcertical board company <br><br>for time period:06/01/2025 To 06/30/2025<br><br>Please check and approve. <br><br>Thank you.','2025-06-30',NULL,NULL,_binary '\0',_binary '\0',NULL,NULL,'','2025-06-01',NULL,NULL,1);
/*!40000 ALTER TABLE `hour_log_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hour_log_file_path`
--

DROP TABLE IF EXISTS `hour_log_file_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hour_log_file_path` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `admin_added_file` bit(1) DEFAULT NULL,
  `file_original_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `hour_log_file_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdgnfp3qw24nhoa50kxts9so5e` (`hour_log_file_id`),
  CONSTRAINT `FKdgnfp3qw24nhoa50kxts9so5e` FOREIGN KEY (`hour_log_file_id`) REFERENCES `hour_log_file` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hour_log_file_path`
--

LOCK TABLES `hour_log_file_path` WRITE;
/*!40000 ALTER TABLE `hour_log_file_path` DISABLE KEYS */;
INSERT INTO `hour_log_file_path` VALUES (1,'2025-06-18 21:39:42.000000','2025-06-18 21:39:42.000000',_binary '\0','Screenshot 2024-01-24 182346.png','/User/shubratodn44985u2/Timesheet/elcertical-board-company/17502611814870Timesheet1.png',1);
/*!40000 ALTER TABLE `hour_log_file_path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `internal_user`
--

DROP TABLE IF EXISTS `internal_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `internal_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `default_user` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `rate` float DEFAULT NULL,
  `rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `rate_type` enum('FIX','PERCENTAGE') DEFAULT NULL,
  `recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `work_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `internal_user`
--

LOCK TABLES `internal_user` WRITE;
/*!40000 ALTER TABLE `internal_user` DISABLE KEYS */;
INSERT INTO `internal_user` VALUES (1,_binary '\0','shubratodn44985+am1@gmail.com','Account Manager','AccountManager','','+1231232',1,'REVENUE','PERCENTAGE',0,''),(2,_binary '\0','shubratodn44985+b2@gmail.com','BMD2','BDM','Fiverr1','11122222',1,'ON_HOURS','FIX',1,'shubratodn44985+s2@gmail.com'),(3,_binary '\0','shubratodn44985+r@gmail.com','recruiter3','Recruiter','fiverr','111111111',1,'G_MARGIN','FIX',1,'shubratodn44985+s2@gmail.com');
/*!40000 ALTER TABLE `internal_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager` (
  `id` int NOT NULL AUTO_INCREMENT,
  `department` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `managerName` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `client_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh3rgqur2465uoiuciyyhft1yw` (`client_id`),
  CONSTRAINT `FKh3rgqur2465uoiuciyyhft1yw` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp_token`
--

DROP TABLE IF EXISTS `otp_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `createdAt` datetime(6) NOT NULL,
  `expiresAt` datetime(6) NOT NULL,
  `otp` varchar(6) NOT NULL,
  `resendAvailableAt` datetime(6) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5hucs7rrk6tw4n7v58gtx2v51` (`user_id`),
  CONSTRAINT `FK5hucs7rrk6tw4n7v58gtx2v51` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp_token`
--

LOCK TABLES `otp_token` WRITE;
/*!40000 ALTER TABLE `otp_token` DISABLE KEYS */;
INSERT INTO `otp_token` VALUES (1,'2025-06-20 05:55:48.865958','2025-06-20 05:58:48.865958','250253','2025-06-20 05:56:08.865958',_binary '\0',1),(2,'2025-06-20 05:56:32.182163','2025-06-20 05:59:32.182163','731020','2025-06-20 05:56:52.182163',_binary '',1),(3,'2025-06-20 05:56:51.271632','2025-06-20 05:59:51.271632','174994','2025-06-20 05:57:11.271632',_binary '\0',1),(4,'2025-06-20 16:55:10.250453','2025-06-20 16:58:10.250453','190760','2025-06-20 16:55:30.250453',_binary '',1),(5,'2025-06-20 17:03:16.760965','2025-06-20 17:06:16.760965','130407','2025-06-20 17:03:36.760965',_binary '',1),(6,'2025-06-20 17:03:39.497399','2025-06-20 17:06:39.497399','271398','2025-06-20 17:03:59.497399',_binary '\0',1),(7,'2025-06-20 17:07:24.984071','2025-06-20 17:10:24.984071','290815','2025-06-20 17:07:44.984071',_binary '\0',1),(8,'2025-06-20 17:11:07.981619','2025-06-20 17:14:07.981619','297841','2025-06-20 17:11:27.981619',_binary '',1),(9,'2025-06-20 17:11:55.357504','2025-06-20 17:14:55.357504','801464','2025-06-20 17:12:15.357504',_binary '\0',1),(10,'2025-06-20 17:25:26.694867','2025-06-20 17:28:26.694867','138207','2025-06-20 17:25:46.694867',_binary '\0',1),(11,'2025-06-20 17:32:29.171528','2025-06-20 17:35:29.171528','468671','2025-06-20 17:32:49.171528',_binary '\0',1),(12,'2025-06-20 17:39:14.245098','2025-06-20 17:42:14.245098','490395','2025-06-20 17:39:34.245098',_binary '\0',1),(13,'2025-06-20 17:42:52.746567','2025-06-20 17:45:52.746567','452239','2025-06-20 17:43:12.746567',_binary '\0',1),(14,'2025-06-20 17:49:17.369178','2025-06-20 17:52:17.369178','555453','2025-06-20 17:49:37.369178',_binary '',1),(15,'2025-06-20 17:56:43.543632','2025-06-20 17:59:43.543632','026603','2025-06-20 17:57:03.543632',_binary '\0',1),(16,'2025-06-22 04:29:56.516555','2025-06-22 04:32:56.516555','160158','2025-06-22 04:30:16.516555',_binary '\0',1),(17,'2025-06-22 04:31:37.165806','2025-06-22 04:34:37.165806','291036','2025-06-22 04:31:57.165806',_binary '\0',1),(18,'2025-06-22 04:32:16.883816','2025-06-22 04:35:16.883816','211353','2025-06-22 04:32:36.883816',_binary '\0',1),(19,'2025-06-22 04:32:45.294673','2025-06-22 04:35:45.294673','349005','2025-06-22 04:33:05.294673',_binary '',1),(20,'2025-06-23 17:32:33.028954','2025-06-23 17:35:33.028954','504985','2025-06-23 17:32:53.028954',_binary '',1),(21,'2025-06-23 17:33:56.366185','2025-06-23 17:36:56.366185','728019','2025-06-23 17:34:16.366185',_binary '\0',1);
/*!40000 ALTER TABLE `otp_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission_plan`
--

DROP TABLE IF EXISTS `permission_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission_plan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `commission` bit(1) DEFAULT NULL,
  `qb_integration` bit(1) DEFAULT NULL,
  `schedular_can_set` bit(1) DEFAULT NULL,
  `template_can_set` bit(1) DEFAULT NULL,
  `user_can_login` bit(1) DEFAULT NULL,
  `user_limit` int DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcggtnuk32x2k9291pqj8vuehu` (`company_id`),
  CONSTRAINT `FKcggtnuk32x2k9291pqj8vuehu` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission_plan`
--

LOCK TABLES `permission_plan` WRITE;
/*!40000 ALTER TABLE `permission_plan` DISABLE KEYS */;
INSERT INTO `permission_plan` VALUES (1,_binary '',_binary '',_binary '',_binary '',_binary '',65,1),(2,_binary '',_binary '',_binary '',_binary '',_binary '',25,2);
/*!40000 ALTER TABLE `permission_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedular`
--

DROP TABLE IF EXISTS `schedular`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedular` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `daily_hours` float DEFAULT NULL,
  `day_off` bit(1) DEFAULT NULL,
  `extra_hours` float DEFAULT NULL,
  `hours_date` date DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `vacation_hours` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedular`
--

LOCK TABLES `schedular` WRITE;
/*!40000 ALTER TABLE `schedular` DISABLE KEYS */;
/*!40000 ALTER TABLE `schedular` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `signature`
--

DROP TABLE IF EXISTS `signature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `signature` (
  `id` int NOT NULL AUTO_INCREMENT,
  `html_data` text,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `signature`
--

LOCK TABLES `signature` WRITE;
/*!40000 ALTER TABLE `signature` DISABLE KEYS */;
/*!40000 ALTER TABLE `signature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `template` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `html_data` text,
  `mail_template_type` enum('approvalTimesheet','followupTimesheet','general','pendingTimesheet','rejectedTimesheet','schedularTimesheet','submission') DEFAULT NULL,
  `role_admin_permission` tinyint(1) NOT NULL DEFAULT '0',
  `role_supervisor_permission` tinyint(1) NOT NULL DEFAULT '0',
  `role_user_permission` tinyint(1) NOT NULL DEFAULT '0',
  `subject` varchar(255) DEFAULT NULL,
  `template_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `time_sheet_submission`
--

DROP TABLE IF EXISTS `time_sheet_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `time_sheet_submission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `approve` bit(1) DEFAULT NULL,
  `approved_date` datetime(6) DEFAULT NULL,
  `key_value` varchar(255) NOT NULL,
  `reject` bit(1) DEFAULT NULL,
  `reject_reason` text,
  `rejected_date` datetime(6) DEFAULT NULL,
  `submit` bit(1) NOT NULL,
  `week_end_date` datetime(6) DEFAULT NULL,
  `week_start_date` datetime(6) DEFAULT NULL,
  `approved_by` int DEFAULT NULL,
  `rejected_by` int DEFAULT NULL,
  `user_detail_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK43pxo8d0vp4efw4l5d3ntoxc` (`approved_by`),
  KEY `FK4j9eg2bbd3dns74qnacmi1tfj` (`rejected_by`),
  KEY `FK8i36mlk5cf9ogkrjrn8oqqci3` (`user_detail_id`),
  CONSTRAINT `FK43pxo8d0vp4efw4l5d3ntoxc` FOREIGN KEY (`approved_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK4j9eg2bbd3dns74qnacmi1tfj` FOREIGN KEY (`rejected_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK8i36mlk5cf9ogkrjrn8oqqci3` FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `time_sheet_submission`
--

LOCK TABLES `time_sheet_submission` WRITE;
/*!40000 ALTER TABLE `time_sheet_submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `time_sheet_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `active` int DEFAULT NULL,
  `client_active` int DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `file_folder` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  `private_sign` text,
  `role` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `work_email` varchar(255) DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FK2yuxsfrkkrnkn5emoobcnnc3r` (`company_id`),
  CONSTRAINT `FK2yuxsfrkkrnkn5emoobcnnc3r` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,1,NULL,'shubratodn44985@gmail.com','shubrato','shubrato','Debnath','$2a$10$.4nbB419B1t3oLB47QNsA.zK5QPC8wmHPTYiKJGiUahcaFTaxKgvm','+12312421',NULL,'ROLE_ADMIN','299e3a36-63e0-4d9b-a36a-a39950a8afa0','shubratodn44985@gmail.com',NULL),(2,1,NULL,'mycomputer44985@gmail.com','superadmin','SuperAdmin','SuperAdmin','$2a$10$.4nbB419B1t3oLB47QNsA.zK5QPC8wmHPTYiKJGiUahcaFTaxKgvm','+123141234',NULL,'ROLE_ADMIN','40da1418-3fe6-4c5b-be53-4a891b7fdacd','mycomputer44985@gmail.com',NULL),(3,1,2,'251-17-005@gmail.com','sourav','Sourav','Debnath','$2a$10$6NiMlx20LP69VPJJwGZZL.AudxhBk19zEws70inkeVwRcoiMAFKsm','+9123123',NULL,'ROLE_USER','2c50a977-0f5d-4287-aa81-0904f68d51c6','',1),(4,1,NULL,'shubratodn44985+s2@gmail.com','superviro2','Superviro2','Fivver','$2a$10$6NiMlx20LP69VPJJwGZZL.AudxhBk19zEws70inkeVwRcoiMAFKsm','1112223333',NULL,'ROLE_SUPERVISOR','0e5552ae-4e0e-455f-8ddb-0e539b0d2e82','shubratodn44985+s2@gmail.com',1),(5,1,1,'shubratodn44985+u2@gmail.com','shubratodn44985u2','shubratodn44985+u2','fiverrr','$2a$10$.4nbB419B1t3oLB47QNsA.zK5QPC8wmHPTYiKJGiUahcaFTaxKgvm','1112223333',NULL,'ROLE_USER','37421260-1bfc-46c6-8503-c65743ab9040','shubratodn44985+s2@gmail.com',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_company`
--

DROP TABLE IF EXISTS `user_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `login_count` int DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK31viftsw6kihfrdybimaj0r3d` (`company_id`),
  KEY `FK7ca8sstytm1n5sg8if4qq2ph8` (`user_id`),
  CONSTRAINT `FK31viftsw6kihfrdybimaj0r3d` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `FK7ca8sstytm1n5sg8if4qq2ph8` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_company`
--

LOCK TABLES `user_company` WRITE;
/*!40000 ALTER TABLE `user_company` DISABLE KEYS */;
INSERT INTO `user_company` VALUES (1,_binary '',26,'ROLE_ADMIN',1,1),(2,_binary '',3,'ROLE_SUPER_ADMIN',2,2),(3,_binary '',NULL,'ROLE_USER',1,3),(4,_binary '',NULL,'ROLE_SUPERVISOR',1,4),(5,_binary '',2,'ROLE_USER',1,5);
/*!40000 ALTER TABLE `user_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_detail`
--

DROP TABLE IF EXISTS `user_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_detail` (
  `user_detail_id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `account_manager_commission` float NOT NULL,
  `account_manager_commission_rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `account_manager_commission_rate_type` tinyint NOT NULL,
  `account_manager_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `account_manager_recurssive_month` int DEFAULT NULL,
  `account_manager_name` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `business_development_manager_commission` float NOT NULL,
  `business_development_manager_commission_rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `business_development_manager_commission_rate_type` tinyint NOT NULL,
  `business_development_manager_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `business_development_manager_recurssive_month` int DEFAULT NULL,
  `business_development_manager_name` varchar(255) DEFAULT NULL,
  `c2c_or_other` float DEFAULT NULL,
  `c2c_or_other_rate_type` tinyint DEFAULT NULL,
  `c2c_or_other_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `c2c_or_other_recurssive_month` int DEFAULT NULL,
  `client_name` varchar(255) DEFAULT NULL,
  `client_rate` float NOT NULL,
  `consultant_rate` float DEFAULT NULL,
  `employer_email` varchar(255) DEFAULT NULL,
  `employer_name` varchar(255) DEFAULT NULL,
  `employer_phone` varchar(255) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `file_folder` varchar(255) DEFAULT NULL,
  `invoice_to` enum('CLIENT','VENDOR') DEFAULT NULL,
  `ptax` float DEFAULT NULL,
  `recruiter_commission` float NOT NULL,
  `recruiter_rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `recruiter_rate_type` tinyint NOT NULL,
  `recruiter_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `recruiter_recurssive_month` int DEFAULT NULL,
  `recruiter_name` varchar(255) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `time_sheet_period` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `w2` float DEFAULT NULL,
  `w2_or_c2c_type` tinyint DEFAULT NULL,
  `account_manager_id` int DEFAULT NULL,
  `business_development_manager_id` int DEFAULT NULL,
  `client_id` int DEFAULT NULL,
  `employer_id` int DEFAULT NULL,
  `recruiter_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `vendor_id` int DEFAULT NULL,
  PRIMARY KEY (`user_detail_id`),
  KEY `FK5re2cte5vmmhdj3itpkvmhll8` (`account_manager_id`),
  KEY `FKmrv3o7d2f4e89wyqk4w7koj5b` (`business_development_manager_id`),
  KEY `FKsu5xrmy950c808fcpgogf614q` (`client_id`),
  KEY `FKntaeqq0347yij84icnh2yk4b5` (`employer_id`),
  KEY `FKb4ggho01s7m2aj45psplk99sp` (`recruiter_id`),
  KEY `FKc2fr118twu8aratnm1qop1mn9` (`user_id`),
  KEY `FK5whb3t4kduh0wy1a4j4fx3o3f` (`vendor_id`),
  CONSTRAINT `FK5re2cte5vmmhdj3itpkvmhll8` FOREIGN KEY (`account_manager_id`) REFERENCES `internal_user` (`id`),
  CONSTRAINT `FK5whb3t4kduh0wy1a4j4fx3o3f` FOREIGN KEY (`vendor_id`) REFERENCES `client` (`id`),
  CONSTRAINT `FKb4ggho01s7m2aj45psplk99sp` FOREIGN KEY (`recruiter_id`) REFERENCES `internal_user` (`id`),
  CONSTRAINT `FKc2fr118twu8aratnm1qop1mn9` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKmrv3o7d2f4e89wyqk4w7koj5b` FOREIGN KEY (`business_development_manager_id`) REFERENCES `internal_user` (`id`),
  CONSTRAINT `FKntaeqq0347yij84icnh2yk4b5` FOREIGN KEY (`employer_id`) REFERENCES `client` (`id`),
  CONSTRAINT `FKsu5xrmy950c808fcpgogf614q` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
  CONSTRAINT `user_detail_chk_1` CHECK ((`account_manager_commission_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_2` CHECK ((`business_development_manager_commission_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_3` CHECK ((`c2c_or_other_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_4` CHECK ((`recruiter_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_5` CHECK ((`w2_or_c2c_type` between 0 and 1))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_detail`
--

LOCK TABLES `user_detail` WRITE;
/*!40000 ALTER TABLE `user_detail` DISABLE KEYS */;
INSERT INTO `user_detail` VALUES (1,'2025-06-18 21:35:12.000000','2025-06-18 21:35:12.000000',1,'REVENUE',1,0,10,'Account Manager ',_binary '','',1,'ON_HOURS',0,1,NULL,'BMD2 Fiverr1',100,0,1,NULL,'Elcertical board company ',45,0,'','','','2025-09-30 00:00:00.000000','elcertical-board-company','CLIENT',10,1,'G_MARGIN',0,1,NULL,'recruiter3 fiverr','2024-06-26 00:00:00.000000','oneMonth','Vendor 5',30,0,1,2,3,NULL,3,5,4),(2,'2025-06-19 22:46:16.000000','2025-06-19 22:46:16.000000',9,'REVENUE',1,0,2,'Account Manager ',_binary '','111111',6,'ON_HOURS',0,1,NULL,'BMD2 Fiverr1',0,1,1,NULL,'A vendor',500,50,'','Third party employer','11122222','2025-06-30 00:00:00.000000','a-vendor','VENDOR',0,12,'G_MARGIN',0,1,NULL,'recruiter3 fiverr','2025-03-01 00:00:00.000000','oneMonth','A vendor',0,1,1,2,1,5,3,3,2);
/*!40000 ALTER TABLE `user_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_file`
--

DROP TABLE IF EXISTS `user_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `exp_date` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk170byb5nt79g5mr4aasp4410` (`user_id`),
  CONSTRAINT `FKk170byb5nt79g5mr4aasp4410` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_file`
--

LOCK TABLES `user_file` WRITE;
/*!40000 ALTER TABLE `user_file` DISABLE KEYS */;
INSERT INTO `user_file` VALUES (1,'2025-06-19 23:56:45.236000','2025-06-19 23:56:45.236000','2025-06-26 00:00:00.000000','Java_1.5_ShubratoDebnath.pdf','/User/sourav/OtherFiles/1750355805216UserFile3.5_ShubratoDebnath.pdf','55456','pdf',3);
/*!40000 ALTER TABLE `user_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_access`
--

DROP TABLE IF EXISTS `user_role_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role_access` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `functionality` enum('ADD_SCHEDULAR','ADD_TIME_SHEET','CLIENT_ACCESS','CLIENT_ASSIGN_USER','CONSULTANT_DASHBOARD','EMPLOYEE_ACCESS','GENERAL_MAIL','HOURS_DASHBOARD','INTERNAL_USER','PENDING_TIMESHEET_MAIL','REPORT_TIME_SHEET','SUBMITTED_TIMESHEET','SUPERVISOR_ACTIVITY','TEMPLATE','TIMESHEET','TIME_SHEET_SCHEDULAR','USER','USER_ACTIVITY','VENDOR_ACCESS') DEFAULT NULL,
  `is_create` bit(1) DEFAULT NULL,
  `is_delete` bit(1) DEFAULT NULL,
  `is_own` bit(1) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `is_update` bit(1) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_access`
--

LOCK TABLES `user_role_access` WRITE;
/*!40000 ALTER TABLE `user_role_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role_access` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-28 21:49:08
