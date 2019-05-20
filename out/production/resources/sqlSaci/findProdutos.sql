select DISTINCT prdno as codigo, grade, TRIM(S.barcode) codebar,
       TRIM(MID(S.name, 1, 37)) as nome, cost/10000 as custo,
       MID(S.name, 38, 3) as unidade,
       CASE
         WHEN P.groupno = 10000 THEN 'CAIXA'
         WHEN P.groupno = 120000 THEN IF(MID(S.name, 38, 3) like 'MT%', 'BOBINA', 'PECA')
         ELSE 'NORMAL'
       END as tipo
from sqlpdv.prdstk AS S
  inner join sqldados.prd AS P
    ON P.no = S.prdno
where no = :prdno
      and storeno IN (1, 2, 3, 4, 5, 6, 7, 10)