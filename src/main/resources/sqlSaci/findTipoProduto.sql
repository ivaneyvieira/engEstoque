select no as prdno, grade as grade, '' as barcode, '' as tipo
from sqldados.prd AS P
where typeno = :typeno
order by no