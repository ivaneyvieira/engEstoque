SELECT no AS prdno
FROM sqldados.prd AS P
WHERE (mfno = :vendno OR :vendno = 0)
  AND (typeno = :typeno OR :typeno = 0)
  AND (clno = :clno OR deptno = :clno OR groupno = :clno OR :clno = 0)
  AND (:pedido = 0)
UNION
SELECT prdno
FROM sqldados.eoprd AS E
WHERE storeno = :storeno
  AND ordno = :pedido
  AND :pedido > 0