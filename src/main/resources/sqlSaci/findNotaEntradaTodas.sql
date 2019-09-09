select N.invno, N.storeno, cast(nfname as char) as numero, invse as serie,
       N.date,
       N.issue_date                         AS dtEmissao,
       CASE
           WHEN invse = '66' then 'ACERTO_E'
           WHEN type = 0 then 'COMPRA'
           WHEN type = 1 then 'TRANSFERENCIA_E'
           WHEN type = 2 then 'DEV_CLI'
           WHEN type = 8 then 'RECLASSIFICACAO_E'
           WHEN type = 10 AND N.remarks LIKE 'DEV%' then 'DEV_CLI'
           ELSE 'NOTA_E'
           END                                       AS tipo,
       N.bits & POW(2, 4) != 0                   AS cancelada
from sqldados.inv AS N
where N.storeno = :storeno
        AND N.date > DATE_SUB(current_date, interval 30 day)