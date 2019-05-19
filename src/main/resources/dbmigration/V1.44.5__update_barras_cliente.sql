update itens_nota AS I
  inner join v_codigo_barra_cliente AS V
    ON I.nota_id = V.id_nota
SET I.codigo_barra_cliente = codbar
