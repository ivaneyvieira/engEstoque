SELECT CAST(MD5(CONCAT(storenoNfr, pdvnoNfr, xanoNfr)) AS CHAR) AS id, awnfr.storenoNfr AS storeno,
       awnfr.pdvnoNfr AS pdvno, awnfr.xanoNfr AS xano, awnfrnf.emissao AS data,
       awnfr.storenoNfr AS storenoFat,
       IFNULL(cast(CONCAT(awnfr.nfno, '/', awnfr.nfse) AS CHAR), '') AS nffat,
       awnfrnf.nfStoreno AS storenoTransf,
       IFNULL(cast(CONCAT(awnfrnf.nfNfno, '/', awnfrnf.nfNfse) AS CHAR), '') AS nftransf
FROM sqldados.awnfrnf
  INNER JOIN sqldados.awnfr
               ON (awnfr.storeno = awnfrnf.awnfrStoreno AND awnfr.cargano = awnfrnf.awnfrCargano)
  INNER JOIN sqldados.nf
               ON (storenoNfr = nf.storeno AND pdvnoNfr = nf.pdvno AND xanoNfr = nf.xano)
WHERE awnfrnf.emissao > :data_inicial AND awnfrnf.nfStoreno = 4 AND nf.status <> 1 AND
      awnfr.storenoNfr <> awnfrnf.nfStoreno
