SELECT TRIM(no) AS codigo, garantia AS mesesGarantia
FROM sqldados.prd AS P
WHERE tipoGarantia = 2
  AND no = LPAD(:codigo, 16, ' ')