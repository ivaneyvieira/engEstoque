select storeno, trim(prdno) as codigo, grade, L.localizacao
from sqldados.prdloc
WHERE (storeno = :storeno)
  AND (prdno = :prdno OR :prdno = '')
  AND (grade = :grade OR :grade = '')
  AND (localizacao LIKE CONCAT(:localizacao, '%') OR :localizacao = '')