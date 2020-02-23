SELECT pxa.storeno, pxa.nfno, pxa.nfse, pxanf2.nfekey
FROM sqlpdv.pxanf2
  INNER JOIN sqlpdv.pxa
               USING (storeno, pdvno, xano)
WHERE nfekey = :nfekey
UNION
DISTINCT
SELECT nf.storeno, nf.nfno, nf.nfse, nf2.nfekey
FROM sqldados.nf2
  INNER JOIN sqldados.nf
               USING (storeno, pdvno, xano)
WHERE nfekey = :nfekey
