SELECT MD5(CONCAT(L.prdno, L.grade, L.storeno, L.localizacao))                       AS id,
       L.storeno                                                                     AS storeno,
       L.prdno                                                                       AS codigo,
       L.grade                                                                       AS grade,
       TRIM(SUBSTR(X.name, 1, 37))                                                   AS nome,
       TRIM(SUBSTR(X.name, 38, 3))                                                   AS unidade,
       IFNULL((SUBSTRING_INDEX(E.text__256, '.', 1) * 1), 0)                         AS comp,
       IFNULL(SUBSTRING_INDEX(SUBSTRING_INDEX(E.text__256, '.', 2), '.', -1) * 1, 0) AS larg,
       IFNULL(SUBSTRING_INDEX(E.text__256, '.', -1) * 1, 0)                          AS alt,
       L.localizacao                                                                 AS localizacao,
       SUBSTRING_INDEX(L.localizacao, '.', 1)                                        AS abreviacao,
       IF(X.tipoGarantia = 2, X.garantia, 0)                                         AS mesesValidade
FROM sqldados.prdloc          AS L
  INNER JOIN sqldados.prd     AS X
	       ON X.no = L.prdno
  LEFT JOIN  sqldados.prdetq2 AS E
	       ON E.prdno = L.prdno AND E.seqno = 1 AND E.text__256 REGEXP '^[0-9]+.[0-9]+.[0-9]+'
GROUP BY L.storeno, L.prdno, L.grade, L.localizacao
HAVING L.localizacao <> ''

