create or replace view v_loc_produtos AS
select MD5(CONCAT(prdno, L.grade, storeno, localizacao)) as 'id',
       storeno,
       prdno                                             as codigo,
       L.grade,
       localizacao,
       substring_index(localizacao, '.', 1)              AS abreviacao,
       P.id                                              as produto_id,
       E.id                                              AS loja_id
from sqldados.prdloc AS L
         inner join engEstoque.produtos AS P
                    ON P.codigo = L.prdno
                        AND P.grade = L.grade
         inner join engEstoque.lojas AS E
                    ON E.numero = L.storeno;