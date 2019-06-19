select no as prdno, grade as grade, '' as barcode, '' as tipo
from sqldados.prd AS P
  inner join engEstoque.produtos AS I
    ON P.no = I.codigo
where groupno = :clno
   OR deptno  = :clno
   OR clno    = :clno
order by no