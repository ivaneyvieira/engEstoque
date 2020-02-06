SELECT no AS prdno, grade AS grade, '' AS barcode, '' AS tipo
FROM sqldados.prd AS P
WHERE typeno = :typeno
ORDER BY no