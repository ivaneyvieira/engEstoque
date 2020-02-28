SELECT 0 AS invno, N.storeno, N.nfno AS numero, N.nfse AS serie, N.issuedate AS date,
       N.issuedate AS dtEmissao, N.status = 1 AS cancelado,
       CASE
         WHEN N.nfse = 1 AND N.cfo IN (5922, 6922)
           THEN 'VENDAF'
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
         ELSE 'SP_REME'
       END AS tipo
FROM sqldados.nf AS N
WHERE N.storeno = :storeno AND N.nfno = :nfno AND N.nfse = :nfse
