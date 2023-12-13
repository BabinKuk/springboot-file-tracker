CREATE DATABASE  IF NOT EXISTS `file_blob_tracker`;
USE `file_blob_tracker`;
--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;

CREATE TABLE `file` (
  `id` int(11) NOT NULL,
  `file_name` varchar(45) DEFAULT NULL,
  `file_desc` varchar(45) DEFAULT NULL,
  `data` MEDIUMBLOB  DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
