SELECT CAST(MD5(CONCAT(N.l2, N.storeno, N.nfno, N.nfse)) AS CHAR) AS id,
       cast(MID(N.l2, 1, 1) AS UNSIGNED)                          AS storenoPedido,
       N.l2                                                       AS ordno,
       N.storeno                                                  AS storenoNota,
       N.nfno,
       N.nfse,
       CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR)                  AS numero,
       N.issuedate                                                AS dataNota,
       N.issuedate                                                AS dataPedido
FROM sqldados.nf AS N
WHERE N.l2 BETWEEN 100000000 AND 999999999
  AND N.issuedate > :data_inicial
GROUP BY id