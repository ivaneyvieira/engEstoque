SELECT
  cast(MD5(CONCAT(storeno, cast(MAX(IF(pxa.cfo IN (5922, 6922), CONCAT(pxa.nfno, '/', pxa.nfse), '0')) as char))) as char) AS id,
  pxa.storeno,  pxa.eordno AS ordno,
  cast(MAX(IF(pxa.cfo IN (5922, 6922), CONCAT(pxa.nfno, '/', pxa.nfse), '0')) as char) as numero_venda,
  MAX(IF(pxa.cfo IN (5922, 6922), pxa.nfno, 0)) as nfno_venda,
  MAX(IF(pxa.cfo IN (5922, 6922), pxa.nfse, 0)) as nfse_venda,

  cast(MAX(IF(pxa.cfo IN (5117, 6117), CONCAT(pxa.nfno, '/', pxa.nfse), '0')) as char) as numero_entrega,
  MAX(IF(pxa.cfo IN (5117, 6117), pxa.nfno, 0)) as nfno_entrega,
  MAX(IF(pxa.cfo IN (5117, 6117), pxa.nfse, 0)) as nfse_entrega,
  IFNULL(cast(MAX(IF(pxa.cfo IN (5117, 6117), pxanf2.nfekey, '')) as char), '') as nfekey_entrega
FROM sqlpdv.pxa
  INNER JOIN sqlpdv.pxanf2
    USING(storeno, pdvno, xano)
  WHERE (pxa.storeno IN (4))
  AND pxa.cfo in (5922, 6922, 5117, 6117)
  AND pxa.date > :data_inicial
group by pxa.storeno, pxa.eordno
having nfno_venda <> 0
