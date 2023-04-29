drop table if exists t_pedido;
CREATE TABLE t_pedido
(
    `storeno`    smallint(5) NOT NULL DEFAULT '0',
    `numero`     int(10)     NOT NULL DEFAULT '0',
    `abreviacao` varchar(60)          DEFAULT NULL,
    PRIMARY KEY (storeno, numero, abreviacao)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;