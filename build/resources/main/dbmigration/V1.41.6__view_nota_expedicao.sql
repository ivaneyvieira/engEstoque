create or replace view v_nota_expedicao AS
select N.*, SUBSTRING_INDEX(I.localizacao, '.', 1) as abreviacao 
  from notas AS N
  inner join itens_nota AS I
  ON I.nota_id = N.id
where I.status = 'INCLUIDA'
GROUP BY N.id, abreviacao