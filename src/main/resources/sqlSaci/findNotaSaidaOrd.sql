SELECT DISTINCT cast(CONCAT(N.paymno) as char)     AS rota,
                N.storeno,
                N.ordno       AS numero,
                ''            AS serie,
                N.date,
                N.date as dtEmissao,
                P.prdno,
                P.grade,
                P.qtty / 1000 AS quant,
                C.name        AS clienteName,
                'PEDIDO_S'    AS tipo
FROM sqldados.eord AS                    N
       INNER JOIN sqldados.eoprd AS      P USING (storeno, ordno)
       INNER JOIN sqldados.prdloc AS E
         ON E.prdno = P.prdno AND E.grade = P.grade AND E.storeno = 4
       LEFT JOIN  sqldados.custp AS      C
         ON C.no = N.custno
WHERE N.paymno = 291 AND
      N.storeno = :storeno AND
      (N.ordno = :nfno)