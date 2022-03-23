SELECT DISTINCT prdno, grade, barcode, 'PDV' AS tipo
FROM sqlpdv.prdstk
WHERE storeno = :storeno
  AND barcode = :barcode
UNION
SELECT DISTINCT prdno, grade, barcode, 'GRADE' AS tipo
FROM sqldados.prdbar
WHERE barcode = :barcode
  AND barcode <> ''
UNION
SELECT DISTINCT no AS prdno, '' AS grade, barcode, 'PRD' AS tipo
FROM sqldados.prd
WHERE barcode = :barcode
  AND barcode <> ''
UNION
SELECT DISTINCT prdno, '' AS grade, auxString1 AS barcode, 'PRD2' AS tipo
FROM sqldados.prd2
WHERE auxString1 = :barcode
  AND auxString1 <> ''
