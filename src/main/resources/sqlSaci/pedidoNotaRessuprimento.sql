SELECT CAST(MD5(CONCAT(O.no, N.storeno, N.nfno, N.nfse)) AS CHAR) AS id,
       cast(MID(O.no, 1, 1) AS UNSIGNED) AS storenoPedido, O.no AS ordno, N.storeno AS storenoNota,
       N.nfno, N.nfse, CAST(CONCAT(N.nfno, '/', N.nfse) AS CHAR) AS numero, N.issuedate AS dataNota,
       O.date AS dataPedido
FROM sqldados.ords       AS O
  INNER JOIN sqldados.nf AS N
               ON N.storeno = 4 AND N.l2 = O.no
WHERE O.date > :data_inicial
  AND N.issuedate > :data_inicial
  AND O.storeno = 1
  AND O.no BETWEEN 100000000 AND 999999999
GROUP BY O.no, N.storeno, N.nfno, N.nfse