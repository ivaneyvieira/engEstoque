SELECT TRIM(P.codigo) AS codigo,
       TP.nome        AS nome,
       P.grade        AS grade,
       TP.codebar     AS codebar,
       I.localizacao  AS localizacao,
       SUM(quantidade * CASE
                            WHEN tipo_mov = 'ENTRADA'
                                THEN 1
                            ELSE -1
           END * CASE
                     WHEN tipo_mov IN ('INCLUIDA', 'ENTREGUE_LOJA') ||
                          tipo_nota IN ('CANCELADA_E', 'CANCELADA_S')
                         THEN 0
                     ELSE 1
               END)   AS quantidade
FROM engEstoque.itens_nota AS I
         INNER JOIN engEstoque.notas AS N
                    ON N.id = I.nota_id
         INNER JOIN engEstoque.lojas AS L
                    ON N.loja_id = L.id
         INNER JOIN engEstoque.produtos AS P
                    ON P.id = I.produto_id
         LEFT JOIN engEstoque.tab_produtos AS TP
                   ON TP.produto_id = P.id
WHERE L.numero = :storeno
  AND (P.codigo = :prdno OR :prdno = '')
GROUP BY I.produto_id, I.localizacao
HAVING CASE :estoque
           WHEN '='
               THEN quantidade = 0
           WHEN '<>'
               THEN quantidade != 0
           WHEN '>'
               THEN quantidade > 0
           WHEN '<'
               THEN quantidade < 0
           WHEN 'T'
               THEN TRUE
           ELSE FALSE
           END