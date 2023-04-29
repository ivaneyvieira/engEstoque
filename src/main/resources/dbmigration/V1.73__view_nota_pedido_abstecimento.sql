CREATE OR REPLACE VIEW v_pedido_abastecimento AS
SELECT max(I.id)                              AS id,
       I.nota_id                              AS nota_id,
       N.numero                               AS numero,
       N.tipo_mov                             AS tipo_mov,
       N.tipo_nota                            AS tipo_nota,
       N.rota                                 AS rota,
       N.data                                 AS data,
       N.hora                                 AS hora,
       N.observacao                           AS observacao,
       N.loja_id                              AS loja_id,
       N.created_at                           AS created_at,
       N.updated_at                           AS updated_at,
       N.version                              AS version,
       N.fornecedor                           AS fornecedor,
       N.cliente                              AS cliente,
       N.data_emissao                         AS data_emissao,
       N.sequencia                            AS sequencia,
       N.usuario_id                           AS usuario_id,
       N.lancamento                           AS lancamento,
       substring_index(I.localizacao, '.', 1) AS abreviacao,
       I.codigo_barra_conferencia
FROM (notas N
    JOIN itens_nota I
      ON ((I.nota_id = N.id)))
WHERE ((I.status = 'INCLUIDA') AND (N.tipo_nota = 'PEDIDO_A'))
GROUP BY N.id, abreviacao;