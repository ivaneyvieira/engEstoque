SELECT DISTINCT
       R.no          AS rota,
       N.storeno,
       N.ordno       AS numero,
       N.date,
       N.date        AS dtEmissao,
       P.prdno,
       P.grade,
       P.qtty / 1000 AS quant,
       C.name        AS clienteName,
       'PEDIDO_S'    AS tipo
FROM sqldados.eord                      AS N
         INNER JOIN sqldados.eoprd      AS P
                    USING (storeno, ordno)
         INNER JOIN engEstoque.produtos AS E
                    ON E.codigo = P.prdno AND E.grade = P.grade
         INNER JOIN sqldados.custp      AS C
                    ON C.no = N.custno
         INNER JOIN sqldados.store      AS S
                    ON C.cpf_cgc = S.cgc
         INNER JOIN sqldados.xroute     AS R
                    ON R.storenoFrom = N.storeno AND R.storenoTo = S.no
WHERE N.storeno = :storeno AND
      N.status = 2
ORDER BY N.date DESC





