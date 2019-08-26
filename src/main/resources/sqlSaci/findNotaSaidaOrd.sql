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
       INNER JOIN engEstoque.produtos AS E
         ON E.codigo = P.prdno AND E.grade = P.grade
       LEFT JOIN  sqldados.custp AS      C
         ON C.no = N.custno
WHERE N.paymno = 291 AND
      N.storeno = :storeno AND
      (N.ordno = :nfno)