select distinct prdno, grade, barcode, 'PDV' as tipo
from sqlpdv.prdstk
where storeno = :storeno
  and prdno = :prdno
  and grade = :grade
union
select distinct prdno, grade, barcode, 'GRADE' as tipo
from sqldados.prdbar
where prdno =  :prdno
  and grade =  :grade
  and :grade != ''
union
select distinct no as prdno, '' as grade, barcode, 'PRD' as tipo
from sqldados.prd
where no    = :prdno
  and :grade = ''