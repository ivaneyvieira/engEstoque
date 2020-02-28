SELECT N.invno, N.storeno, cast(nfname AS CHAR) AS numero, invse AS serie, N.date,
       N.bits & POW(2, 4) != 0 AS cancelado,
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
FROM sqldados.inv AS N
WHERE N.storeno = :storeno AND N.nfname = :nfno AND N.invse = :nfse