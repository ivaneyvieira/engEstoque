SELECT DISTINCT S.prdno AS codigo,
S.grade AS grade,
TRIM ('') AS codebar,
TRIM (substr (X.name, 1, 37)) AS nome,
(0 / 10000) AS custo,
substr (X.name, 38, 3) AS unidade,
CASE WHEN (X.clno BETWEEN 10000 AND 19999) THEN  'CAIXA' WHEN (X.clno BETWEEN 120000 AND 129999) THEN if ((substr (X.name, 38, 3) LIKE  'MT%'), 'BOBINA', 'PECA') ELSE  'NORMAL' END AS tipo,
ifnull ((substring_index (E.text__256, '.', 1) * 1), 0) AS comp,
ifnull ((substring_index (substring_index (E.text__256, '.', 2), '.', - (1)) * 1), 0) AS larg,
ifnull ((substring_index (E.text__256, '.', - (1)) * 1), 0) AS alt FROM sqldados.prdloc S INNER JOIN sqldados.prd AS X ON X.no = S.prdno LEFT JOIN sqldados.prdetq2 AS E ON E.prdno = S.prdno AND E.seqno = 1 AND E.text__256 regexp  '^[0-9]+.[0-9]+.[0-9]+'