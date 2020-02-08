SELECT P.invno, N.storeno, cast(nfname AS CHAR) AS numero, invse AS serie,
       CAST(IFNULL(X.xrouteno, '') AS CHAR) AS rota, N.date, N.issue_date AS dtEmissao,
       P.prdno AS prdno, P.grade, P.qtty / 1000 AS quant, V.name AS vendName,
       CASE
         WHEN invse = '66'
           THEN 'ACERTO_E'
         WHEN type = 0
           THEN 'COMPRA'
         WHEN type = 1
           THEN 'TRANSFERENCIA_E'
         WHEN type = 2
           THEN 'DEV_CLI'
         WHEN type = 8
           THEN 'RECLASSIFICACAO_E'
         WHEN type = 10 AND N.remarks LIKE 'DEV%'
           THEN 'DEV_CLI'
         ELSE 'NOTA_E'
       END AS tipo
FROM sqldados.inv          AS N
  INNER JOIN sqldados.iprd AS P
               USING (invno)
  INNER JOIN sqldados.vend AS V
               ON V.no = N.vendno
  LEFT JOIN  sqldados.xfr  AS X
               ON X.no = N.xfrno
WHERE N.invno = (SELECT MAX(invno)
                 FROM sqldados.inv AS N
                 WHERE N.storeno = :storeno
                   AND nfname = :nfname
                   AND invse = :invse
                   AND N.bits & POW(2, 4) = 0
                   AND N.auxShort13 & pow(2, 15) = 0
                   AND invse <> '')
UNION
SELECT DISTINCT 0 AS invno, N.storeno, cast(ordno AS CHAR) AS numero, '' AS serie,
                cast(CONCAT(N.paymno) AS CHAR) AS rota, N.date, N.date, P.prdno AS prdno, P.grade,
                P.qtty / 1000 AS quant, C.name AS vendName, 'PEDIDO_E' AS tipo
FROM sqldados.eord           AS N
  INNER JOIN sqldados.eoprd  AS P
               USING (storeno, ordno)
  INNER JOIN sqldados.prdloc AS E
               ON E.prdno = P.prdno AND E.grade = P.grade AND E.storeno = 4
  LEFT JOIN  sqldados.custp  AS C
               ON C.no = N.custno
WHERE N.paymno = 290
  AND N.storeno = :storeno
  AND (N.ordno = :nfname)
  AND (:invse = '')