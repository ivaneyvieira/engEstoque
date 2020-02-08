SELECT no AS prdno, grade AS grade, '' AS barcode, '' AS tipo
FROM sqldados.prd AS P
WHERE groupno = :clno
   OR deptno = :clno
   OR clno = :clno
ORDER BY no