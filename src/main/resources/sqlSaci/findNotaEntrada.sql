
select P.invno, N.storeno, cast(nfname as char) as numero, invse as serie,
       CAST(IFNULL(X.xrouteno, '') AS CHAR) AS rota,
       N.date,
       N.issue_date                         AS dtEmissao,
       P.prdno                              AS prdno,
       P.grade, P.qtty/1000                 as quant, V.name as vendName,
  CASE
    WHEN invse = '66' then 'ACERTO_E'
    WHEN type = 0 then 'COMPRA'
    WHEN type = 1 then 'TRANSFERENCIA_E'
    WHEN type = 2 then 'DEV_CLI'
    WHEN type = 8 then 'RECLASSIFICACAO_E'
    WHEN type = 10 AND N.remarks LIKE 'DEV%' then 'DEV_CLI'
    ELSE 'NOTA_E'
  END                                       AS tipo
from sqldados.inv AS N
  inner join sqldados.iprd AS P
  USING(invno)
  inner join sqldados.vend AS V
    ON V.no = N.vendno
  left join sqldados.xfr AS X
    ON X.no = N.xfrno
where N.invno = (SELECT MAX(invno)
                 FROM sqldados.inv AS N
                 where N.storeno = :storeno
                     and nfname = :nfname
                     and invse = :invse
                     AND N.bits & POW(2, 4) = 0
                     AND N.auxShort13 & pow(2, 15) = 0
                     AND invse <> ''
)
UNION
select 0                    as invno, N.storeno, cast(ordno as char) as numero, '' as serie,
       cast(CONCAT(N.paymno) as char)      AS rota,
       N.date,
       N.date,
       P.prdno              AS prdno,
       P.grade, P.qtty/1000 as quant, C.name as vendName,
       'PEDIDO_E'           AS tipo
from sqldados.eord AS N
  inner join sqldados.eoprd AS P
  USING(storeno, ordno)
  inner join engEstoque.produtos AS E
    ON E.codigo = P.prdno AND E.grade = P.grade
  left join sqldados.custp AS C
    ON C.no = N.custno
WHERE N.paymno = 290 AND
      N.storeno = :storeno
      and (N.ordno = :nfname)
      and (:invse = '')