select distinct prdno, grade, barcode, 'PDV' as tipo
from sqlpdv.prdstk
where storeno = :storeno
  and barcode = :barcode
union
select distinct prdno, grade, barcode, 'GRADE' as tipo
from sqldados.prdbar
where barcode = :barcode
  and grade != ''
  and barcode <> ''
union
select distinct no as prdno, '' as grade, barcode, 'PRD' as tipo
from sqldados.prd
where barcode = :barcode
  and barcode <> ''
union
select distinct prdno, '' as grade, auxString1 as barcode, 'PRD2' as tipo
from sqldados.prd2
where auxString1 = :barcode
  and auxString1 <> ''
