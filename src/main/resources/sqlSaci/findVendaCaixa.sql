SELECT cast(MD5(CAST(CONCAT(storeno, pdvno, xano, prdno, grade) AS CHAR)) AS CHAR) AS id, P.storeno,
       P.nfno, P.nfse, prdno, grade, SUM(I.qtty / 1000) AS qtty
FROM sqlpdv.pxa            AS P
  INNER JOIN sqlpdv.pxaprd AS I
               USING (storeno, pdvno, xano)
WHERE P.date = :data_atual AND
      storeno = 4 AND
      nfse BETWEEN 10 AND 20
GROUP BY storeno, pdvno, xano, prdno, grade
