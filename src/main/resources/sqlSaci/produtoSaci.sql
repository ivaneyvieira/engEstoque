SELECT CAST(MD5(CONCAT(L.storeno, P.no, L.grade, L.localizacao)) AS CHAR) AS id, L.storeno,
       P.no AS codigo, IFNULL(TRIM(MID(P.name, 1, 37)), '') AS nome, L.grade, L.localizacao,
       IFNULL(MID(L.localizacao, 1, 4), '') AS abreviacao, 0.0000 AS custo,
       IFNULL(TRIM(MID(P.name, 38, 3)), '') AS unidade,
       (CASE
          WHEN (P.clno BETWEEN 10000 AND 19999)
            THEN 'CAIXA'
          WHEN (P.clno BETWEEN 120000 AND 129999)
            THEN if((substr(P.name, 38, 3) LIKE 'MT%'), 'BOBINA', 'PECA')
          ELSE 'NORMAL'
        END) AS tipo, ifnull((substring_index(E.text__256, '.', 1) * 1), 0) AS comp,
       ifnull((substring_index(substring_index(E.text__256, '.', 2), '.', -(1)) * 1), 0) AS larg,
       ifnull((substring_index(E.text__256, '.', -(1)) * 1), 0) AS alt
FROM sqldados.prd            AS P
  INNER JOIN sqldados.prdloc AS L
               ON P.no = L.prdno AND L.storeno = 4
  LEFT JOIN  sqldados.prdetq2   E
               ON E.prdno = L.prdno AND E.seqno = 1 AND E.text__256 REGEXP '^[0-9]+.[0-9]+.[0-9]+'
GROUP BY id