DROP TABLE IF EXISTS `devolucao_fornecedor`;
CREATE TABLE `t_devolucao_fornecedor` (
  `id`          VARBINARY(32) NOT NULL DEFAULT '',
  `storeno`     SMALLINT(5)   NOT NULL DEFAULT '0',
  `pdvno`       SMALLINT(5)   NOT NULL DEFAULT '0',
  `xano`        INT(10)       NOT NULL DEFAULT '0',
  `invno`       INT(10)       NOT NULL DEFAULT '0',
  `nfeno`       CHAR(32)      NOT NULL DEFAULT '',
  `nfese`       CHAR(4)       NOT NULL DEFAULT '',
  `nfsno`       INT(10)       NOT NULL DEFAULT '0',
  `nfsse`       CHAR(2)       NOT NULL DEFAULT '',
  `localizacao` CHAR(60)      NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE (storeno, pdvno, xano, invno, localizacao)
)