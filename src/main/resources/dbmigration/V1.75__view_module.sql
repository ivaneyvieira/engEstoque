DROP VIEW v_pedido_abastecimento;

DROP VIEW v_nota_futura;

DROP VIEW v_pedido_ressuprimento;

DROP VIEW v_nota_expedicao;

CREATE VIEW v_pedido_abastecimento AS
SELECT max(I.id) AS id, I.nota_id AS nota_id, N.numero AS numero, N.tipo_mov AS tipo_mov,
       N.tipo_nota AS tipo_nota, N.rota AS rota, N.data AS data, N.hora AS hora,
       N.observacao AS observacao, N.loja_id AS loja_id, N.created_at AS created_at,
       N.updated_at AS updated_at, N.version AS version, N.fornecedor AS fornecedor,
       N.cliente AS cliente, N.data_emissao AS data_emissao, N.sequencia AS sequencia,
       N.usuario_id AS usuario_id, N.lancamento AS lancamento,
       substring_index(I.localizacao, '.', 1) AS abreviacao,
       I.codigo_barra_conferencia AS codigo_barra_conferencia
FROM (notas N
  JOIN itens_nota I
         ON ((I.nota_id = N.id)))
WHERE ((I.status = 'INCLUIDA') AND (N.lancamento_origem = 'ABASTECI'))
GROUP BY N.id, abreviacao;

CREATE VIEW v_nota_futura AS
SELECT max(I.id) AS id, I.nota_id AS nota_id, N.numero AS numero, N.tipo_mov AS tipo_mov,
       N.tipo_nota AS tipo_nota, N.rota AS rota, N.data AS data, N.hora AS hora,
       N.observacao AS observacao, N.loja_id AS loja_id, N.created_at AS created_at,
       N.updated_at AS updated_at, N.version AS version, N.fornecedor AS fornecedor,
       N.cliente AS cliente, N.data_emissao AS data_emissao, N.sequencia AS sequencia,
       N.usuario_id AS usuario_id, N.lancamento AS lancamento,
       substring_index(I.localizacao, '.', 1) AS abreviacao
FROM (notas N
  JOIN itens_nota I
         ON ((I.nota_id = N.id)))
WHERE ((I.status = 'INCLUIDA') AND (N.lancamento_origem = 'ENTREGA_F'))
GROUP BY N.id, abreviacao;

CREATE VIEW v_pedido_ressuprimento AS
SELECT max(I.id) AS id, I.nota_id AS nota_id, N.numero AS numero, N.tipo_mov AS tipo_mov,
       N.tipo_nota AS tipo_nota, N.rota AS rota, N.data AS data, N.hora AS hora,
       N.observacao AS observacao, N.loja_id AS loja_id, N.created_at AS created_at,
       N.updated_at AS updated_at, N.version AS version, N.fornecedor AS fornecedor,
       N.cliente AS cliente, N.data_emissao AS data_emissao, N.sequencia AS sequencia,
       N.usuario_id AS usuario_id, N.lancamento AS lancamento,
       substring_index(I.localizacao, '.', 1) AS abreviacao,
       I.codigo_barra_conferencia AS codigo_barra_conferencia
FROM (notas N
  JOIN itens_nota I
         ON ((I.nota_id = N.id)))
WHERE ((I.status = 'INCLUIDA') AND (N.lancamento_origem = 'RESSUPRI'))
GROUP BY N.id, abreviacao;

CREATE VIEW v_nota_expedicao AS
SELECT max(I.id) AS id, I.nota_id AS nota_id, N.numero AS numero, N.tipo_mov AS tipo_mov,
       N.tipo_nota AS tipo_nota, N.rota AS rota, N.data AS data, N.hora AS hora,
       N.observacao AS observacao, N.loja_id AS loja_id, N.created_at AS created_at,
       N.updated_at AS updated_at, N.version AS version, N.fornecedor AS fornecedor,
       N.cliente AS cliente, N.data_emissao AS data_emissao, N.sequencia AS sequencia,
       N.usuario_id AS usuario_id, N.lancamento AS lancamento,
       substring_index(I.localizacao, '.', 1) AS abreviacao
FROM (notas N
  JOIN itens_nota I
         ON ((I.nota_id = N.id)))
WHERE ((I.status = 'INCLUIDA') AND (N.lancamento_origem = 'EXPEDICAO'))
GROUP BY N.id, abreviacao;

