SELECT cast(MD5(CONCAT(storeno, cast(
  MAX(IF(pxa.cfo IN (5922, 6922), CONCAT(pxa.nfno, '/', pxa.nfse), '0')) AS CHAR))) AS CHAR) AS id,
       pxa.storeno, pxa.eordno AS ordno,
       cast(MAX(IF(pxa.cfo IN (5922, 6922), CONCAT(pxa.nfno, '/', pxa.nfse),
                   '0')) AS CHAR) AS numero_venda,
       MAX(IF(pxa.cfo IN (5922, 6922), pxa.nfno, 0)) AS nfno_venda,
       MAX(IF(pxa.cfo IN (5922, 6922), pxa.nfse, 0)) AS nfse_venda,

       cast(MAX(IF(pxa.cfo IN (5117, 6117), CONCAT(pxa.nfno, '/', pxa.nfse),
                   '0')) AS CHAR) AS numero_entrega,
       MAX(IF(pxa.cfo IN (5117, 6117), pxa.nfno, 0)) AS nfno_entrega,
       MAX(IF(pxa.cfo IN (5117, 6117), pxa.nfse, 0)) AS nfse_entrega,
       IFNULL(cast(MAX(IF(pxa.cfo IN (5117, 6117), pxanf2.nfekey, '')) AS CHAR),
              '') AS nfekey_entrega
FROM sqlpdv.pxa
  INNER JOIN sqlpdv.pxanf2
               USING (storeno, pdvno, xano)
WHERE (pxa.storeno IN (4))
  AND pxa.cfo IN (5922, 6922, 5117, 6117)
  AND pxa.date > :data_inicial
GROUP BY pxa.storeno, pxa.eordno
HAVING nfno_venda <> 0
