DROP TABLE IF EXISTS TNOT_LOC;
CREATE TEMPORARY TABLE TNOT_LOC
(
    PRIMARY KEY (item_nota_id)
)
SELECT I.id AS item_nota_id, IFNULL(L2.localizacao, L1.localizacao) AS localizacao, L2.abreviacao
FROM itens_nota AS I
         LEFT JOIN usuarios AS U
                   ON I.usuario_id = U.id
         LEFT JOIN produtos AS P
                   ON P.id = I.produto_id
         LEFT JOIN v_loc_produtos AS L1
                   ON L1.produto_id = P.id
         LEFT JOIN v_loc_produtos AS L2
                   ON L2.produto_id = P.id AND LOCATE(L2.abreviacao, U.localizacaoes) > 0;

UPDATE itens_nota AS I INNER JOIN TNOT_LOC AS L
    ON L.item_nota_id = I.id
SET I.localizacao = L.localizacao;