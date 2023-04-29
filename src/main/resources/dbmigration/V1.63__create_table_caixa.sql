DROP TABLE IF EXISTS t_vendas_caixa;

CREATE TABLE t_vendas_caixa
(
    id      varchar(32)    NOT NULL,
    storeno smallint(5)    NULL,
    nfno    int(10)        NULL,
    nfse    char(2)        NULL,
    prdno   char(16)       NULL,
    grade   char(8)        NULL,
    qtty    decimal(36, 4) NULL,
    PRIMARY KEY (id) USING BTREE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 0;