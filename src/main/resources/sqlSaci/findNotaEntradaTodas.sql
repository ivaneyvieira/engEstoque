SELECT N.invno,
       N.storeno,
       cast(nfname AS CHAR)                  AS numero,
       invse                                 AS serie,
       N.date,
       N.issue_date                          AS dtEmissao,
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
           END                               AS tipo,
       IF(N.bits & POW(2, 4) != 0, 'S', 'N') AS cancelado,
       I.prdno,
       I.grade
FROM sqldados.iprd AS I
         LEFT JOIN sqldados.inv AS N
                   USING (invno)
         LEFT JOIN sqldados.prdloc AS L
                   ON L.storeno = I.storeno AND L.prdno = I.prdno AND L.grade = I.grade
WHERE I.storeno = :storeno
  AND L.localizacao LIKE :abreviacao
  AND I.date > :data
  AND nfname IS NOT NULL
GROUP BY I.invno, I.prdno, I.grade
