CREATE OR REPLACE VIEW v_codigo_barra_conferencia AS
SELECT I.id                                   AS id_itens_nota,
       REPLACE(
               concat(L.numero, ' ', N.numero, ' ', N.sequencia, ' ', substring_index(I.localizacao, '.', 1)),
               '/',
               ' '
           )
                                              AS codbar,
       L.numero                               as storeno,
       N.numero,
       N.sequencia,
       substring_index(I.localizacao, '.', 1) as abreviacao
FROM itens_nota AS I
         JOIN notas AS N
              ON N.id = I.nota_id
         JOIN lojas AS L
              ON N.loja_id = L.id;

CREATE OR REPLACE VIEW v_codigo_barra_entrega AS
select I.id                                   AS id_itens_nota,
       replace(concat(L.numero, ' ', N.numero, ' ', N.sequencia, ' ', substring_index(I.localizacao, '.', 1),
                      ' ', trim(P.codigo), ' ', P.grade, ' ', I.quantidade),
               '/', ' ')                      AS codbar,
       L.numero                               as storeno,
       N.numero,
       N.sequencia,
       substring_index(I.localizacao, '.', 1) AS abreviacao,
       P.codigo,
       P.grade,
       I.quantidade
from itens_nota I
         join notas N on N.id = I.nota_id
         join lojas L on N.loja_id = L.id
         join produtos P on P.id = I.produto_id;