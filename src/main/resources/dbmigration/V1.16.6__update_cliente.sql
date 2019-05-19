
drop temporary table if exists T;
create temporary table T
select I.storeno as storenoT, CAST(IFNULL(X.xrouteno, '') AS CHAR) as rota, N.storeno,
  N.nfno, N.nfse, N.issuedate as date,P.prdno as prdno, P.grade, P.qtty as quant,
  C.name as clienteName,
  CASE
    WHEN N.nfse = '66' then 'ACERTO_S'
    WHEN N.nfse = '3' then 'ENT_RET'
    WHEN tipo = 0 then 'VENDA'
    WHEN tipo = 1 then 'TRANSFERENCIA_S'
    WHEN tipo = 2 then 'DEV_FOR'
    ELSE 'INVALIDA'
  END AS tipo
from sqldados.nf AS N
  inner join sqldados.xaprd AS P
  USING(storeno, pdvno, xano)
  inner join engEstoque.produtos AS E
  ON E.codigo = P.prdno AND E.grade = P.grade
  left join sqldados.custp AS C
    ON C.no = N.custno
  left join sqldados.inv AS I
    ON N.invno = I.invno
  left join sqldados.xfr AS X
    ON X.no = I.xfrno
where N.issuedate > DATE_SUB(current_date, INTERVAL 12 MONTH)
      AND N.status <> 1
UNION
select DISTINCT  null as storenoT, '' as rota, N.storeno,
  N.nfno, N.nfse, N.date,P.prdno, P.grade, P.qtty/1000 as quant,
  C.name as clienteName,
  CASE
    WHEN N.nfse = '66' then 'ACERTO_S'
    WHEN N.nfse = '3' then 'ENT_RET'
    ELSE 'VENDA'
  END AS tipo
from sqlpdv.pxa AS N
  inner join sqlpdv.pxaprd AS P
  USING(storeno, pdvno, xano)
  inner join engEstoque.produtos AS E
    ON E.codigo = P.prdno AND E.grade = P.grade
  left join sqldados.custp AS C
    ON C.no = N.custno
UNION
select DISTINCT  null as storenoT, '' as rota, N.storeno,
  N.ordno as nfno, '' as nfse, N.date,P.prdno, P.grade, P.qtty/1000 as quant,
  C.name as clienteName,
  'PEDIDO_S' AS tipo
from sqldados.eord AS N
  inner join sqldados.eoprd AS P
  USING(storeno, ordno)
  inner join engEstoque.produtos AS E
    ON E.codigo = P.prdno AND E.grade = P.grade
  left join sqldados.custp AS C
    ON C.no = N.custno;

drop temporary table if exists T2;
create temporary table T2
(primary key(storeno, nfno, nfse))
select storeno, nfno, nfse, clienteName
from T
group by storeno, nfno, nfse;

UPDATE notas AS N
  inner join lojas AS L
    ON N.loja_id = L.id
  inner join T2
    ON T2.storeno = L.numero
    AND T2.nfno regexp CONCAT('0*', SUBSTRING_INDEX(N.numero, '/', 1))
    AND T2.nfse = SUBSTRING_INDEX(N.numero, '/', -1)
SET N.cliente = T2.clienteName,
    N.fornecedor = ''
WHERE tipo_mov = 'SAIDA';