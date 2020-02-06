DROP TABLE IF EXISTS t_pedido_nota_ressuprimento;
CREATE TABLE t_pedido_nota_ressuprimento (
  id            VARCHAR(32)          DEFAULT NULL,
  storenoPedido INT(10)              DEFAULT NULL,
  ordno         INT(10)     NOT NULL DEFAULT '0',
  dataPedido    INT(10)     NOT NULL,
  storenoNota   SMALLINT(5) NOT NULL DEFAULT '0',
  nfno          INT(10)     NOT NULL DEFAULT '0',
  nfse          CHAR(2)     NOT NULL DEFAULT '',
  numero        VARCHAR(14)          DEFAULT NULL,
  dataNota      INT(10)     NOT NULL
)