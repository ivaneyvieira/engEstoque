DROP TABLE IF EXISTS t_dados_produto_saci;

CREATE TABLE `t_dados_produto_saci` (
  `id` varchar(32) DEFAULT NULL,
  `storeno` smallint(5) NOT NULL DEFAULT '0',
  `codigo` char(16) NOT NULL DEFAULT '',
  `grade` char(8) NOT NULL DEFAULT '',
  `nome` varchar(37) DEFAULT NULL,
  `unidade` varchar(3) DEFAULT NULL,
  `comp` double NOT NULL DEFAULT '0',
  `larg` double NOT NULL DEFAULT '0',
  `alt` double NOT NULL DEFAULT '0',
  `localizacao` char(60) NOT NULL DEFAULT '',
  `abreviacao` varchar(60) DEFAULT NULL
) ENGINE=InnoDB;