SELECT N.storeno,
       cast(nfname AS CHAR)    AS numero,
       invse                   AS serie,
       N.date,
       N.issue_date            AS dtEmissao,
       CASE WHEN invse = '66'
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
            ELSE 'NOTA_E' END  AS tipo,
       N.bits & POW(2, 4) != 0 AS cancelado
FROM sqldados.inv                   AS N
         INNER JOIN sqldados.iprd   AS I
                    USING (invno)
         INNER JOIN sqldados.prdloc AS L
                    ON L.storeno = I.storeno AND L.prdno = I.prdno AND L.grade = I.grade
WHERE N.storeno = :storeno AND
      L.localizacao LIKE :abreviacao AND
      N.date > DATE_SUB(current_date, INTERVAL 30 DAY)
GROUP BY invno