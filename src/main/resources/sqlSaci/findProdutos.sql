SELECT DISTINCT prdno                    AS codigo,
                grade,
                TRIM(S.barcode)          AS codebar,
                TRIM(MID(S.name, 1, 37)) AS nome,
                cost / 10000             AS custo,
                MID(S.name, 38, 3)       AS unidade,
                CASE
                    WHEN P.groupno = 10000
                        THEN 'CAIXA'
                    WHEN P.groupno = 120000
                        THEN IF(MID(S.name, 38, 3) LIKE 'MT%', 'BOBINA', 'PECA')
                    ELSE 'NORMAL'
                    END                  AS tipo
FROM sqlpdv.prdstk AS S
         INNER JOIN sqldados.prd AS P
                    ON P.no = S.prdno
WHERE no = :prdno
  AND storeno IN (1, 2, 3, 4, 5, 6, 7, 10)