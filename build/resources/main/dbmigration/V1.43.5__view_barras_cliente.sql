CREATE OR REPLACE VIEW v_codigo_barra_cliente AS
SELECT
	N.id AS id_nota,
	REPLACE(concat(L.numero,' ',N.numero,' ',N.sequencia,' '), '/',	' ') AS codbar,
	L.numero    AS storeno,
	N.numero    AS numero,
	N.sequencia AS sequencia
FROM
	notas N
		JOIN lojas L
		ON N.loja_id = L.id