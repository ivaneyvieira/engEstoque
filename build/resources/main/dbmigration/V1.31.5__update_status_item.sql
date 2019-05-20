update itens_nota AS I
  inner join notas AS N
    ON N.id = I.nota_id
SET I.status = IF(tipo_mov = 'ENTRADA', 'RECEBIDO', 'ENTREGUE');