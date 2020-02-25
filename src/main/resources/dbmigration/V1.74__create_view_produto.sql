CREATE OR REPLACE VIEW v_loc_produtos AS
SELECT md5(concat(L.codigo, L.grade, L.storeno, L.localizacao)) AS id, L.storeno AS storeno,
       L.codigo, L.grade AS grade, L.localizacao AS localizacao, L.abreviacao, P.id AS produto_id,
       E.id AS loja_id
FROM t_produto_saci L
  JOIN produtos     P
         ON P.codigo = L.codigo AND P.grade = L.grade
  JOIN lojas        E
         ON E.numero = L.storeno;

CREATE OR REPLACE VIEW view_produtos AS
SELECT DISTINCT T.codigo, T.grade AS grade, '' AS codebar, nome, custo, unidade, tipo, comp, larg,
                alt, P.id AS produto_id
FROM t_produto_saci AS T
  JOIN produtos        P
         ON T.codigo = P.codigo AND T.grade = P.grade;

CREATE OR REPLACE VIEW v_produtos_saci AS
SELECT md5(concat(T.storeno, T.codigo)) AS id, T.codigo AS codigo, T.grade AS grade, '' AS codebar,
       T.nome AS nome, 0 AS custo, T.unidade AS unidade, '' AS tipo, T.localizacao AS localizacao
FROM engEstoque.t_dados_produto_saci T
GROUP BY T.storeno, T.codigo