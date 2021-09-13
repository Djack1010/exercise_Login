CREATE TABLE IF NOT EXISTS `login`.`login` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` varchar(25) NOT NULL,
  `password` varchar(100) NOT NULL,
  `privatekey` varchar(100) NOT NULL
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
