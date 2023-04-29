DROP TABLE IF EXISTS `t_entrega_futura`;

CREATE TABLE `t_entrega_futura`
(
    `id`             VARCHAR(32) NOT NULL DEFAULT '',
    `storenoVenda`   INT(11)              DEFAULT NULL,
    `numeroVenda`    VARCHAR(14)          DEFAULT NULL,
    `nfnoVenda`      BIGINT(11)           DEFAULT NULL,
    `nfseVenda`      VARCHAR(2)           DEFAULT NULL,
    `dataVenda`      INT(11)              DEFAULT NULL,
    `storenoEntrega` INT(11)              DEFAULT NULL,
    `numeroEntrega`  VARCHAR(14)          DEFAULT NULL,
    `nfnoEntrega`    BIGINT(11)           DEFAULT NULL,
    `nfseEntrega`    VARCHAR(2)           DEFAULT NULL,
    `dataEntrega`    INT(11)              DEFAULT NULL,
    `nfekeyEntrega`  VARCHAR(44)          DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `storeno` (`numeroVenda`),
    KEY `nfekey_entrega` (`nfekeyEntrega`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = latin1