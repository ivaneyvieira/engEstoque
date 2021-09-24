DROP TEMPORARY TABLE IF EXISTS T_VENC;
CREATE TEMPORARY TABLE T_VENC (
  PRIMARY KEY (id)
)
SELECT I.id,
       N.data_emissao,
       P.meses_vencimento,
       DATE_ADD(N.data_emissao, INTERVAL P.meses_vencimento MONTH) AS dataVencimento
FROM engEstoque.itens_nota       AS I
  INNER JOIN engEstoque.produtos AS P
	       ON P.id = I.produto_id
  INNER JOIN engEstoque.notas    AS N
	       ON N.id = I.nota_id
WHERE P.meses_vencimento IS NOT NULL;

UPDATE engEstoque.itens_nota AS I
  inner join T_VENC using(id)
set I.data_vencimento = dataVencimento
where I.data_vencimento IS NULL;