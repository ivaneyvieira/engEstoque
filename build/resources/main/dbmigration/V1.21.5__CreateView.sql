DROP TABLE IF EXISTS T;
CREATE TEMPORARY TABLE T (
  PRIMARY KEY (nota)
)
    SELECT storeno, concat(nfname, '/', invse) AS nota, MAX(issue_date) AS data
    FROM sqldados.inv
    WHERE storeno = 4 AND
          date > DATE_SUB(current_date, INTERVAL 3 MONTH) AND
          invse <> ''
    GROUP BY nota;

UPDATE engEstoque.notas AS N
SET data_emissao = data;

UPDATE engEstoque.notas AS N INNER JOIN T
ON T.nota = N.numero AND N.tipo_mov = 'ENTRADA'
SET N.data_emissao = T.data;