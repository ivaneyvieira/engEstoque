select distinct prdno, grade
from sqlpdv.prdstk
where storeno = :storeno
  and barcode*1 = :barcode