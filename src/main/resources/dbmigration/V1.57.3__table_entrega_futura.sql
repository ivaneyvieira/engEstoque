
DROP TABLE IF EXISTS t_entrega_futura;
CREATE TABLE t_entrega_futura (
  `id` varchar(32) NOT NULL DEFAULT '',
  `storeno` smallint(5) NOT NULL DEFAULT '0',
  `ordno` int(10) NOT NULL DEFAULT '0',
  `numero_venda` varchar(14) DEFAULT NULL,
  `nfno_venda` bigint(11) DEFAULT NULL,
  `nfse_venda` varchar(2) DEFAULT NULL,
  `numero_entrega` varchar(14) DEFAULT NULL,
  `nfno_entrega` bigint(11) DEFAULT NULL,
  `nfse_entrega` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `storeno` (`storeno`,`numero_venda`)
);