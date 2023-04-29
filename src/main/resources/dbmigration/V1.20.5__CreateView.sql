CREATE OR REPLACE VIEW v_loc_localizacao AS
SELECT DISTINCT loja_id, localizacao
FROM v_loc_produtos;

CREATE OR REPLACE VIEW v_loc_abreviacao AS
SELECT DISTINCT loja_id, abreviacao
FROM v_loc_produtos;

CREATE OR REPLACE VIEW view_produtos AS
SELECT DISTINCT S.prdno                                                                     AS codigo,
                S.grade                                                                     AS grade,
                TRIM(S.barcode)                                                             AS codebar,
                TRIM(substr(S.name, 1, 37))                                                 AS nome,
                (0 / 10000)                                                                 AS custo,
                substr(S.name, 38, 3)                                                       AS unidade,
                (CASE
                     WHEN (S.clno BETWEEN 10000 AND 19999)
                         THEN 'CAIXA'
                     WHEN (S.clno BETWEEN 120000 AND 129999)
                         THEN if((substr(S.name, 38, 3) LIKE 'MT%'), 'BOBINA', 'PECA')
                     ELSE 'NORMAL'
                    END)                                                                    AS tipo,
                IFNULL(SUBSTRING_INDEX(text__256, '.', 1) * 1, 0)                           AS comp,
                IFNULL(SUBSTRING_INDEX(SUBSTRING_INDEX(text__256, '.', 2), '.', -1) * 1, 0) AS larg,
                IFNULL(SUBSTRING_INDEX(text__256, '.', -1) * 1, 0)                          AS alt,
                P.id                                                                        AS produto_id
FROM (sqlpdv.prdstk S INNER JOIN engEstoque.produtos P
      ON (((S.prdno = P.codigo) AND (S.grade = P.grade))))
         LEFT JOIN sqldados.prdetq2 AS E
                   ON E.prdno = S.prdno AND E.seqno = 1 AND E.text__256 REGEXP '^[0-9]+\.[0-9]+\.[0-9]+';