DROP TABLE IF EXISTS t_produto_saci;
CREATE TABLE t_produto_saci (
  id          VARCHAR(32)    NOT NULL DEFAULT '',
  storeno     SMALLINT(5)    NOT NULL DEFAULT '0',
  codigo      CHAR(16)       NOT NULL DEFAULT '',
  nome        VARCHAR(37)    NOT NULL DEFAULT '',
  grade       CHAR(8)        NOT NULL DEFAULT '',
  localizacao CHAR(60)       NOT NULL DEFAULT '',
  abreviacao  VARCHAR(4)     NOT NULL DEFAULT '',
  custo       DECIMAL(13, 4) NOT NULL DEFAULT '0.0000',
  unidade     VARCHAR(3)     NOT NULL DEFAULT '',
  tipo        VARCHAR(10)    NOT NULL DEFAULT '',
  comp        DOUBLE         NOT NULL DEFAULT '0',
  larg        DOUBLE         NOT NULL DEFAULT '0',
  alt         DOUBLE         NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  KEY (storeno, codigo, grade),
  KEY (localizacao),
  KEY (abreviacao)
)
  ENGINE = MyISAM
  DEFAULT CHARSET = latin1