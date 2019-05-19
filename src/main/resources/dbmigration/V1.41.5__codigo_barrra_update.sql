update itens_nota AS I
  inner join v_codigo_barra_conferencia AS V
    ON I.id = V.id_itens_nota
  inner join notas as N
    ON N.id = I.nota_id
set codigo_barra_conferencia = IF(sequencia = 0, numero, codbar);
