SELECT
    CAST(MD5(CONCAT(storenoNfr, pdvnoNfr, xanoNfr)) AS CHAR)                      AS  id,
    awnfr.storenoNfr                                                              AS  storeno,
    awnfr.pdvnoNfr                                                                AS  pdvno,
    awnfr.xanoNfr                                                                 AS  xano,
    awnfrnf.emissao                                                               AS  data,

    awnfr.storenoNfr                                                              AS  storenoFat,
    cast(CONCAT(awnfr.nfno,"/",awnfr.nfse) as char)                               AS  nffat,

    awnfrnf.nfStoreno                                                             AS  storenoTransf,
    cast(CONCAT(awnfrnf.nfNfno,"/",awnfrnf.nfNfse) as char)                       AS  nftransf
FROM sqldados.awnfrnf
   INNER JOIN sqldados.awnfr  ON (awnfr.storeno = awnfrnf.awnfrStoreno and awnfr.cargano=awnfrnf.awnfrCargano)
WHERE
   (awnfrnf.emissao > :data_inicial)
   AND awnfrnf.nfStoreno = 4
GROUP BY  storenoNfr, pdvnoNfr, xanoNfr;