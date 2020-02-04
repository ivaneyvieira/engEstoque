SELECT pxa.storeno, pxa.nfno, pxa.nfse, pxanf2.nfekey
FROM sqlpdv.pxanf2
  INNER JOIN sqlpdv.pxa
               USING (storeno, pdvno, xano)
WHERE nfekey = :nfekey