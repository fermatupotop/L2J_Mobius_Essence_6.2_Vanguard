DROP TABLE IF EXISTS `grandboss_data`;
CREATE TABLE IF NOT EXISTS `grandboss_data` (
  `boss_id` smallint(5) unsigned NOT NULL,
  `loc_x` mediumint(6) NOT NULL,
  `loc_y` mediumint(6) NOT NULL,
  `loc_z` mediumint(6) NOT NULL,
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawn_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `currentHP` decimal(30,15) NOT NULL,
  `currentMP` decimal(30,15) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`boss_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

INSERT IGNORE INTO `grandboss_data` (`boss_id`,`loc_x`,`loc_y`,`loc_z`,`heading`,`currentHP`,`currentMP`) VALUES
(29001, 93069, 8925, -3904, 0, 9000000, 667.776), -- Queen Ant
(29006, 17726, 108915, -6480, 0, 9000000, 3793.536), -- Core
(29014, 64418, 29467, -3792, 10126, 9000000, 3793.536), -- Orfen
(29020, 115779, 17199, 10073, -25348, 4068372, 39960), -- Baium
(29022, 52207, 217230, -3341, 0, 9000000, 12240), -- Zaken
-- (29028, -105200, -253104, -15264, 0, 62041918, 2248572), -- Valakas
(29068, 125976, 124842, -3952, 32768, 62802301, 1998000); -- Antharas
-- (25286, -6675, 18505, -5488, 16550, 556345880, 86847), -- Anakim
-- (25283, 185059, -9610, -5488, 15640, 486021997, 79600), -- Lilith


