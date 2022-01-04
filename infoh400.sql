-- MySQL dump 10.16  Distrib 10.1.8-MariaDB, for Win32 (AMD64)
--
-- Host: localhost    Database: infoh400
-- ------------------------------------------------------
-- Server version	10.1.8-MariaDB

CREATE DATABASE infoh400;
CREATE USER 'infoh400'@'localhost' IDENTIFIED BY 'student400';
GRANT ALL PRIVILEGES ON infoh400.* TO 'infoh400'@'localhost';
USE infoh400;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointment` (
  `idappointment` int(11) NOT NULL AUTO_INCREMENT,
  `idpatient` int(11) NOT NULL,
  `iddoctor` int(11) NOT NULL,
  `appointmenttime` datetime DEFAULT NULL,
  `reason` text,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`idappointment`),
  KEY `appointment_patient_fk` (`idpatient`),
  KEY `appointment_doctor_fk` (`iddoctor`),
  CONSTRAINT `appointment_doctor_fk` FOREIGN KEY (`iddoctor`) REFERENCES `doctor` (`iddoctor`),
  CONSTRAINT `appointment_patient_fk` FOREIGN KEY (`idpatient`) REFERENCES `patient` (`idpatient`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctor` (
  `iddoctor` int(11) NOT NULL AUTO_INCREMENT,
  `idperson` int(11) NOT NULL,
  `inami` varchar(20) NOT NULL,
  `specialty` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`iddoctor`),
  KEY `doctor_person_fk` (`idperson`),
  CONSTRAINT `doctor_person_fk` FOREIGN KEY (`idperson`) REFERENCES `person` (`idperson`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor`
--

LOCK TABLES `doctor` WRITE;
/*!40000 ALTER TABLE `doctor` DISABLE KEYS */;
INSERT INTO `doctor` VALUES (1,2,'12345678','General Practitioner');
/*!40000 ALTER TABLE `doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `image` (
  `idimage` int(11) NOT NULL AUTO_INCREMENT,
  `instanceuid` varchar(100) NOT NULL,
  `studyuid` varchar(100) DEFAULT NULL,
  `seriesuid` varchar(100) DEFAULT NULL,
  `patient_dicom_identifier` varchar(100) DEFAULT NULL,
  `idpatient` int(11) DEFAULT NULL,
  `idappointment` int(11) DEFAULT NULL,
  `iddoctor` int(11) DEFAULT NULL,
  PRIMARY KEY (`idimage`),
  KEY `image_appointment_fk` (`idappointment`),
  KEY `image_patient_fk` (`idpatient`),
  KEY `image_doctor_fk` (`iddoctor`),
  CONSTRAINT `image_appointment_fk` FOREIGN KEY (`idappointment`) REFERENCES `appointment` (`idappointment`),
  CONSTRAINT `image_doctor_fk` FOREIGN KEY (`iddoctor`) REFERENCES `doctor` (`iddoctor`),
  CONSTRAINT `image_patient_fk` FOREIGN KEY (`idpatient`) REFERENCES `patient` (`idpatient`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `note` (
  `idnote` int(11) NOT NULL AUTO_INCREMENT,
  `idappointment` int(11) DEFAULT NULL,
  `dateadded` datetime NOT NULL,
  `content` text,
  PRIMARY KEY (`idnote`),
  KEY `note_appointment_fk` (`idappointment`),
  CONSTRAINT `note_appointment_fk` FOREIGN KEY (`idappointment`) REFERENCES `appointment` (`idappointment`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note`
--

LOCK TABLES `note` WRITE;
/*!40000 ALTER TABLE `note` DISABLE KEYS */;
/*!40000 ALTER TABLE `note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `idpatient` int(11) NOT NULL AUTO_INCREMENT,
  `idperson` int(11) NOT NULL,
  `phonenumber` varchar(20) DEFAULT NULL,
  `status` enum('active','inactive','banned') NOT NULL DEFAULT 'active',
  PRIMARY KEY (`idpatient`),
  KEY `patient_person_fk` (`idperson`),
  CONSTRAINT `patient_person_fk` FOREIGN KEY (`idperson`) REFERENCES `person` (`idperson`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (1,1,'0123456789','active');
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `idperson` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(255) DEFAULT NULL,
  `familyname` varchar(255) DEFAULT NULL,
  `dateofbirth` date DEFAULT NULL,
  PRIMARY KEY (`idperson`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'Adrien','Foucart','1988-04-11'),(2,'John','Doe','1970-12-01');
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-01-08 10:00:36
