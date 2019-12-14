select no as prdno, grade as grade, '' as barcode, '' as tipo
from sqldados.prd AS P
where groupno = :clno
   OR deptno  = :clno
   OR clno    = :clno
order by no