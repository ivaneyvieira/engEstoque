SELECT TRIM(no)                             AS codigo,
       IF(tipoGarantia = 2, garantia, NULL) AS mesesGarantia,
       ROUND(qttyPackClosed / 1000)         AS quantidadePacote
FROM sqldados.prd AS P
