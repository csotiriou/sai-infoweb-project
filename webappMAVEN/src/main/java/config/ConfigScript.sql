-- @author Christos Sotiriou
-- This is the actual script that fills the database. 
-- Run this instead of the obsolete "Serv.java" servlet
-- instead.


CREATE DATABASE IF NOT EXISTS oradb DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE oradb;

CREATE TABLE students (
    studentid int(11) NOT NULL AUTO_INCREMENT,
    name varchar(45) DEFAULT NULL,
    lastname varchar(45) NOT NULL,
    email varchar(128) NOT NULL,
    apikey varchar(128) DEFAULT NULL,
    registered tinyint(4) NOT NULL DEFAULT '0',
    registrationkey varchar(128) DEFAULT NULL,
    PRIMARY KEY (studentid),
    UNIQUE KEY email_UNIQUE (email),
    UNIQUE KEY registrationkey_UNIQUE (registrationkey),
    UNIQUE KEY apikey_UNIQUE (apikey)
);


CREATE TABLE device (
    deviceid int(11) NOT NULL AUTO_INCREMENT,
    identifier TEXT NOT NULL,
    studentid int(11) DEFAULT NULL,
    macaddress varchar(45) NOT NULL,
    platform varchar(45) NOT NULL,
    registered tinyint(4) NOT NULL DEFAULT '0',
    apikey varchar(128),
    PRIMARY KEY (deviceid),
    UNIQUE KEY deviceid_UNIQUE (deviceid),
    UNIQUE KEY macaddress_UNIQUE (macaddress)
);
CREATE TABLE oradb.serviceusr (
    userid INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(45) NOT NULL,
    userlevel TINYINT NOT NULL DEFAULT 1,
    userpass VARCHAR(45) NOT NULL DEFAULT '',
    PRIMARY KEY (userid , username),
    UNIQUE INDEX username_UNIQUE (username ASC)
);


CREATE TABLE `messages` (
  `messageid` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(256) NOT NULL,
  `userid` int(11) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`messageid`,`date`),
  UNIQUE KEY `messageid_UNIQUE` (`messageid`)
);



CREATE TABLE registration (
    registrationid int(11) NOT NULL AUTO_INCREMENT,
    studentid int(11) NOT NULL,
    registrationtoken varchar(128) NOT NULL,
    deviceid int(11) NOT NULL,
    PRIMARY KEY (registrationid),
    UNIQUE KEY registrationtoken_UNIQUE (registrationtoken),
    UNIQUE KEY deviceid_UNIQUE (deviceid),
    UNIQUE KEY registrationid_UNIQUE (registrationid)
);


CREATE TABLE authentication (
    authenticationid int(11) NOT NULL AUTO_INCREMENT,
    token varchar(128) NOT NULL,
    studentid int(11) DEFAULT NULL,
    expiry datetime NOT NULL,
    PRIMARY KEY (authenticationid , token),
    UNIQUE KEY token_UNIQUE (token)
);



CREATE TABLE apikeys (
    keyid int(11) NOT NULL AUTO_INCREMENT,
    keytoken varchar(128) NOT NULL,
    PRIMARY KEY (keyid , keytoken),
    UNIQUE KEY idapikeys_UNIQUE (keyid),
    UNIQUE KEY keytoken_UNIQUE (keytoken)
);

CREATE  TABLE `oradb`.`messageassociations` (
  `messageid` INT NOT NULL ,
  `studentid` INT NOT NULL ,
  PRIMARY KEY (`messageid`, `studentid`) 
);


CREATE TABLE `pref` (
  `prefid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`prefid`,`name`),
  UNIQUE KEY `idpref_UNIQUE` (`prefid`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);


INSERT INTO serviceusr (`username`, `userlevel`, `userpass` ) VALUES ('root', 1, '');




-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `handleRegistration`(
    IN regToken varchar(128)
)
BEGIN 
    DECLARE stID INT;
    DECLARE devID INT;
    
    SELECT studentid, deviceid INTO stID, devID
        FROM registration
        WHERE registrationtoken LIKE regToken;

    UPDATE device SET registered='1' where deviceid=devID;
    UPDATE students SET registered='1' where studentid=stID; 
    DELETE FROM registration WHERE registrationToken=regToken; 
END

-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registerStudent`(
     IN studentName varchar(48),
     IN surname varchar(48),
     IN studentEmail varchar(70),
     IN apns varchar(128),
     IN mac varchar(128),
     IN plat varchar(10),
     OUT alreadyExists INT,
     OUT outStudentID INT,
     OUT regToken varchar(128),
     OUT devID INT,
     OUT regID INT
)
BEGIN
     DECLARE resultStudentEmail varchar (70);
     DECLARE resultRegistrationToken varchar (128);
     DECLARE tempDeviceID INT DEFAULT 0;
     DECLARE registrationIDTemp INT DEFAULT 0;

     SET alreadyExists = (exists (select d.studentid from device d, students s
          where d.studentid = s.studentid
          and s.email not like studentEmail
          and d.macaddress like mac));
     IF (alreadyExists = 0) then
          INSERT INTO students (`name`, `lastname`, `email`)
               VALUES (studentName,surname,studentEmail)
          ON DUPLICATE KEY UPDATE
               `lastname`=VALUES(`lastname`),
               `name`=VALUES(`name`),
               studentid=LAST_INSERT_ID(studentid);

          SET outStudentID = LAST_INSERT_ID();
         
          INSERT INTO device(identifier, studentid, macaddress, platform, apikey)
               VALUES (apns,  outStudentID, mac, plat, UUID() )
          ON DUPLICATE KEY UPDATE
               identifier=values(identifier),
               studentid=values(studentid),
               deviceid=LAST_INSERT_ID(deviceid);

          SET tempDeviceID = LAST_INSERT_ID();

          INSERT INTO registration (studentid, registrationtoken, deviceid) 
               values (outStudentID, UUID(),tempDeviceID)
          ON DUPLICATE KEY UPDATE
               studentid=VALUES(studentid),
               registrationtoken=VALUES(registrationToken),
               deviceid=VALUES(deviceid);

          SET registrationIDTemp = LAST_INSERT_ID();
         
          SELECT registrationtoken, registrationid into regToken, regID
               FROM registration where deviceid = tempDeviceID;
          SET devID = tempDeviceID;
     END IF;
END