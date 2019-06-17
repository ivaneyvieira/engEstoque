select no as prdno, grade as grade, '' as barcode, '' as tipo
from sqldados.prd AS P
  inner join engEstoque.produtos AS I
    ON P.no = I.codigo
where mfno = :vendno
order by no