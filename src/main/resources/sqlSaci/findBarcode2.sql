select distinct prdno, grade, barcode, 'PDV' as tipo
from sqlpdv.prdstk
where storeno = :storeno
  and prdno = :prdno
  and grade = :grade
  and length(trim(barcode)) = 13
union
select distinct prdno, grade, barcode, 'GRADE' as tipo
from sqldados.prdbar
where prdno =  :prdno
  and grade =  :grade
  and :grade != ''
  and length(trim(barcode)) = 13
union
select distinct no as prdno, '' as grade, barcode, 'PRD' as tipo
from sqldados.prd
where no    = :prdno
  and :grade = ''
  and length(trim(barcode)) = 13
union
select prdno, '' as grade, auxString1 as barcode, 'PRD2' as tipo
from prd2
where prdno  = :prdno
  and :grade = ''
  and length(trim(auxString1)) = 13;