SELECT DISTINCT prdno, grade, barcode, 'PDV' AS tipo
FROM sqlpdv.prdstk
WHERE storeno = :storeno
  AND prdno = :prdno
  AND grade = :grade
  AND barcode <> ''
UNION
SELECT DISTINCT prdno, grade, barcode, 'GRADE' AS tipo
FROM sqldados.prdbar
WHERE prdno = :prdno
  AND grade = :grade
  AND barcode <> ''
UNION
SELECT DISTINCT no AS prdno, '' AS grade, barcode, 'PRD' AS tipo
FROM sqldados.prd
WHERE no = :prdno
  AND :grade = ''
  AND barcode <> ''
UNION
SELECT DISTINCT prdno, '' AS grade, auxString1 AS barcode, 'PRD2' AS tipo
FROM sqldados.prd2
WHERE prdno = :prdno
  AND :grade = ''
  AND auxString1 <> ''
