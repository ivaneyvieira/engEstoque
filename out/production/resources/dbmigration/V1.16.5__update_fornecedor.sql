drop temporary table if exists T;
create temporary table T
select P.invno, N.storeno, nfname as numero, invse as serie,
  CAST(IFNULL(X.xrouteno, '') AS CHAR) as rota, N.date, P.prdno as prdno,
  P.grade, P.qtty/1000 as quant, P.cost/10000 as custo, V.name as vendName,
  CASE
    WHEN invse = '66' then 'ACERTO_E'
    WHEN type = 0 then "COMPRA"
    WHEN type = 1 then "TRANSFERENCIA_E"
    WHEN type = 2 then "DEV_CLI"
    ELSE "INVALIDA"
  END AS tipo, IFNULL(L.localizacao, '') AS localizacao
from sqldados.inv AS N
  inner join sqldados.iprd AS P
  USING(invno)
  inner join sqldados.vend AS V
    ON V.no = N.vendno
  left join sqldados.xfr AS X
    ON X.no = N.xfrno
  left join sqldados.prdloc AS L
    ON N.storeno = L.storeno
    AND P.prdno = L.prdno
    AND P.grade = L.grade
where N.date > DATE_SUB(current_date, INTERVAL 12 MONTH)
      AND N.bits & POW(2, 4) = 0
      AND N.auxShort13 & pow(2, 15) = 0
      AND invse <> ''
UNION
select 0 as invno, N.storeno, ordno as numero, '' as serie,
  '' as rota, N.date, P.prdno as prdno,
  P.grade, P.qtty/1000 as quant, 0/10000 as custo, C.name as vendName,
  'PEDIDO_E' AS tipo, IFNULL(L.localizacao, '') AS localizacao
from sqldados.eord AS N
  inner join sqldados.eoprd AS P
  USING(storeno, ordno)
  inner join engEstoque.produtos AS E
    ON E.codigo = P.prdno AND E.grade = P.grade
  left join sqldados.custp AS C
    ON C.no = N.custno
  left join sqldados.prdloc AS L
    ON N.storeno = L.storeno
    AND P.prdno = L.prdno
    AND P.grade = L.grade;

drop temporary table if exists T2;
create temporary table T2
(primary key(storeno, numero, serie))
select storeno, numero, serie, vendName
from T
group by storeno, numero, serie;

UPDATE notas AS N
  inner join lojas AS L
    ON N.loja_id = L.id
  inner join T2
    ON T2.storeno = L.numero
    AND T2.numero regexp CONCAT('0*', SUBSTRING_INDEX(N.numero, '/', 1))
    AND T2.serie = SUBSTRING_INDEX(N.numero, '/', -1)
SET N.fornecedor = T2.vendName,
    N.cliente = ''
WHERE tipo_mov = 'ENTRADA';

