SELECT 0                          AS invno,
       N.storeno,
       N.nfno                     AS numero,
       N.nfse                     AS serie,
       N.issuedate                AS date,
       N.issuedate                AS dtEmissao,
       IF(N.status = 1, 'S', 'N') AS cancelado,
       CASE WHEN N.nfse = 1 AND N.cfo IN (5922, 6922)
                THEN ''
            WHEN N.nfse = '66'
                THEN 'ACERTO_S'
            WHEN N.nfse = '3'
                THEN 'ENT_RET'
            WHEN tipo = 0
                THEN 'VENDA'
            WHEN tipo = 1
                THEN 'TRANSFERENCIA_S'
            WHEN tipo = 2
                THEN 'DEV_FOR'
            WHEN tipo = 3
                THEN 'OUTRAS_NFS'
            WHEN tipo = 7
                THEN 'OUTRAS_NFS'
            ELSE 'SP_REME' END    AS tipo,
       X.prdno,
       X.grade
FROM sqldados.xaprd2               AS X
         INNER JOIN sqldados.nf     AS N
                   USING (storeno, pdvno, xano)
         INNER JOIN sqldados.prdloc AS L
                   ON L.storeno = X.storeno AND L.prdno = X.prdno AND L.grade = X.grade
WHERE X.storeno = :storeno AND
      L.localizacao LIKE :abreviacao AND
      X.date > DATE_SUB(current_date, INTERVAL 30 DAY)
HAVING tipo <> ''

