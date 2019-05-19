create or replace view v_codigo_barra_conferencia AS
select I.id as id_itens_nota, CONCAT(L.numero, ' ', N.numero, ' ', N.sequencia, ' ',
   SUBSTRING_INDEX(I.localizacao, '.', 1)) as codbar
FROM itens_nota AS I
  inner join notas AS N
    ON N.id = I.nota_id
  inner join lojas AS L
    ON N.loja_id = L.id;

create or replace view v_codigo_barra_entrega AS
select I.id as id_itens_nota, CONCAT(L.numero, ' ', N.numero, ' ', N.sequencia, ' ',
   SUBSTRING_INDEX(I.localizacao, '.', 1), ' ', TRIM(P.codigo), ' ', grade, ' ', quantidade) as codbar
FROM itens_nota AS I
  inner join notas AS N
    ON N.id = I.nota_id
  inner join lojas AS L
    ON N.loja_id = L.id
  inner join produtos AS P
    ON P.id = I.produto_id;
