drop table if exists t_pedido;
CREATE TABLE t_pedido (
    `id` varchar(32) DEFAULT NULL,
    `rota` int(10) NOT NULL DEFAULT '0',
    `storeno` smallint(5) NOT NULL DEFAULT '0',
    `numero` int(10) NOT NULL DEFAULT '0',
    `date` int(10) NOT NULL DEFAULT '0',
    `clienteName` char(40) NOT NULL DEFAULT '',
    `abreviacao` varchar(60) DEFAULT NULL,
    `nfno` int(10) DEFAULT '0',
    `nfse` char(2) DEFAULT '',
    `status` smallint(5) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;