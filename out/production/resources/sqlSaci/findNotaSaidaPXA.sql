SELECT DISTINCT ''            AS rota,
                N.storeno,
                N.nfno AS numero,
                N.nfse AS serie,
                N.date,
                N.date as dt_emissao,
                P.prdno,
                P.grade,
                SUM(P.qtty / 1000) AS quant,
                C.name        AS clienteName,
                CASE WHEN N.nfse = '66'
                          THEN 'ACERTO_S'
                     WHEN N.nfse = '3'
                          THEN 'ENT_RET'
                     ELSE 'VENDA'
                    END       AS tipo
FROM sqlpdv.pxa AS                       N
       INNER JOIN sqlpdv.pxaprd AS       P USING (storeno, pdvno, xano)
       INNER JOIN engEstoque.produtos AS E
         ON E.codigo = P.prdno AND E.grade = P.grade
       LEFT JOIN  sqldados.custp AS      C
         ON C.no = N.custno
WHERE N.storeno = :storeno AND
      N.nfno = :nfno AND
      N.nfse = :nfse AND
      processed = 0
GROUP BY P.storeno, P.pdvno, P.xano, P.prdno, P.grade