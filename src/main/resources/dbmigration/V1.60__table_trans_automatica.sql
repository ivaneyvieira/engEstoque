DROP TABLE IF EXISTS t_transferencia_automatica;
CREATE TABLE `t_transferencia_automatica`
(
    `id`            varchar(32) NOT NULL DEFAULT '',
    `storeno`       smallint(5) NOT NULL DEFAULT '0',
    `pdvno`         smallint(5) NOT NULL DEFAULT '0',
    `xano`          int(10)     NOT NULL DEFAULT '0',
    `data`          int(10)     NOT NULL DEFAULT '0',
    `storenoFat`    smallint(5) NOT NULL DEFAULT '0',
    `nffat`         varchar(14)          DEFAULT NULL,
    `storenoTransf` smallint(5) NOT NULL DEFAULT '0',
    `nftransf`      varchar(14)          DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `storeno` (`storeno`, `pdvno`, `xano`),
    KEY `storenoFat` (`storenoFat`, `nffat`)
)