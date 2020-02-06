SELECT storeno, trim(prdno) AS codigo, grade, L.localizacao
FROM sqldados.prdloc
WHERE (storeno = :storeno)
  AND (prdno = :prdno OR :prdno = '')
  AND (grade = :grade OR :grade = '')
  AND (localizacao LIKE CONCAT(:localizacao, '%') OR :localizacao = '')