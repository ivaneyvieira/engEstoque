select distinct prdno, grade, barcode, 'PDV' as tipo
from sqlpdv.prdstk
where storeno = :storeno
  and barcode = :barcode
union
select distinct prdno, grade, barcode, 'GRADE' as tipo
from sqldados.prdbar
where barcode = :barcode
  and grade != ''
union
select distinct no as prdno, '' as grade, barcode, 'PRD' as tipo
from sqldados.prd
where barcode = :barcode
