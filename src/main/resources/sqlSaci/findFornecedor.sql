select no as prdno, grade as grade, '' as barcode, '' as tipo
from sqldados.prd AS P
where mfno = :vendno
order by no