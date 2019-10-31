drop table if exists t_pedido;
CREATE TABLE t_pedido (
    `id` varchar(32) NOT NULL,
    `storeno` smallint(5) NOT NULL DEFAULT '0',
    `numero` int(10) NOT NULL DEFAULT '0',
    `abreviacao` varchar(60) DEFAULT NULL,
    `nfno` varchar(20) DEFAULT NULL,
    `nfse` varchar(2) DEFAULT NULL,
    `status` int(10) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;