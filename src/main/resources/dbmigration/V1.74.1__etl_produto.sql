DROP TABLE IF EXISTS t_entrega_futura;
CREATE TABLE t_entrega_futura (
  id             VARCHAR(32) NOT NULL,
  storenoVenda   INT(11)     NOT NULL,
  numeroVenda    VARCHAR(14) NOT NULL,
  nfnoVenda      BIGINT(11)  NOT NULL,
  nfseVenda      VARCHAR(2)  NOT NULL,
  dataVenda      INT(11)     NOT NULL,
  storenoEntrega INT(11)     NOT NULL,
  numeroEntrega  VARCHAR(14) NOT NULL,
  nfnoEntrega    BIGINT(11)  NOT NULL,
  nfseEntrega    VARCHAR(2)  NOT NULL,
  dataEntrega    INT(11)     NOT NULL,
  nfekeyEntrega  VARCHAR(44) NULL,
  PRIMARY KEY (id),
  KEY storeno(numeroVenda),
  KEY nfekey_entrega(nfekeyEntrega)
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS t_pedido_nota_ressuprimento;
CREATE TABLE t_pedido_nota_ressuprimento (
  id            VARCHAR(32) NOT NULL,
  storenoPedido INT(10)     NOT NULL,
  ordno         INT(10)     NOT NULL,
  dataPedido    INT(10)     NOT NULL,
  storenoNota   SMALLINT(5) NOT NULL,
  nfno          INT(10)     NULL,
  nfse          CHAR(2)     NULL,
  numero        VARCHAR(14) NOT NULL,
  dataNota      INT(10)     NOT NULL
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS t_pedido;
CREATE TABLE t_pedido (
  id          VARCHAR(32) NOT NULL,
  rota        INT(10)     NOT NULL,
  storeno     SMALLINT(5) NOT NULL,
  numero      INT(10)     NOT NULL,
  date        INT(10)     NOT NULL,
  clienteName CHAR(40)    NOT NULL,
  abreviacao  VARCHAR(60) NOT NULL,
  nfno        INT(10)     NOT NULL,
  nfse        CHAR(2)     NOT NULL,
  status      SMALLINT(5) NOT NULL
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS t_produto_saci;
CREATE TABLE t_produto_saci (
  id          VARCHAR(32)    NOT NULL,
  storeno     SMALLINT(5)    NOT NULL,
  codigo      CHAR(16)       NOT NULL,
  nome        VARCHAR(37)    NOT NULL,
  grade       CHAR(8)        NOT NULL,
  localizacao CHAR(60)       NOT NULL,
  abreviacao  VARCHAR(4)     NOT NULL,
  custo       DECIMAL(13, 4) NOT NULL,
  unidade     VARCHAR(3)     NOT NULL,
  tipo        VARCHAR(10)    NOT NULL,
  comp        DOUBLE         NOT NULL,
  larg        DOUBLE         NOT NULL,
  alt         DOUBLE         NOT NULL,
  PRIMARY KEY (id),
  KEY storeno(storeno, codigo, grade),
  KEY localizacao(localizacao),
  KEY abreviacao(abreviacao),
  KEY i1(codigo, grade)
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS t_transferencia_automatica;
CREATE TABLE t_transferencia_automatica (
  id            VARCHAR(32) NOT NULL,
  storeno       SMALLINT(5) NOT NULL,
  pdvno         SMALLINT(5) NOT NULL,
  xano          INT(10)     NOT NULL,
  data          INT(10)     NOT NULL,
  storenoFat    SMALLINT(5) NOT NULL,
  nffat         VARCHAR(14) NOT NULL,
  storenoTransf SMALLINT(5) NOT NULL,
  nftransf      VARCHAR(14) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY storeno(storeno, pdvno, xano),
  KEY storenoFat(storenoFat, nffat)
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS t_vendas_caixa;
CREATE TABLE t_vendas_caixa (
  id      VARCHAR(32)    NOT NULL,
  storeno SMALLINT(5)    NOT NULL,
  nfno    INT(10)        NOT NULL,
  nfse    CHAR(2)        NOT NULL,
  prdno   CHAR(16)       NOT NULL,
  grade   CHAR(8)        NOT NULL,
  qtty    DECIMAL(36, 4) NOT NULL,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;