SELECT DISTINCT '' AS rota, N.storeno, N.nfno AS numero, N.nfse AS serie, N.date,
                N.date AS dtEmissao, P.prdno, P.grade, (P.qtty / 1000) AS quant,
                C.name AS clienteName,
                CASE
                  WHEN N.nfse = 1 AND N.cfo IN (5922, 6922)
                    THEN 'VENDAF'
                  WHEN N.nfse = '66'
                    THEN 'ACERTO_S'
                  WHEN N.nfse = '3'
                    THEN 'ENT_RET'
                  ELSE 'VENDA'
                END AS tipo, '' AS abreviacao
FROM sqlpdv.pxa              AS N
  INNER JOIN sqlpdv.pxaprd   AS P
               USING (storeno, pdvno, xano)
  INNER JOIN sqldados.prdloc AS E
               ON E.prdno = P.prdno AND E.grade = P.grade AND E.storeno = 4
  LEFT JOIN  sqldados.custp  AS C
               ON C.no = N.custno
WHERE N.storeno = :storeno AND
      N.nfno = :nfno AND
      N.nfse = :nfse AND
      processed = 0
GROUP BY P.storeno, P.pdvno, P.xano, P.prdno, P.grade