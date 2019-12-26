SELECT	md5(concat(L.prdno,L.grade,L.storeno,L.localizacao))			AS id,
	L.storeno						AS storeno,
	L.prdno 						AS codigo,
	L.grade 						AS grade,
	TRIM(substr(X.name,1,37)) 				AS nome,
	TRIM(substr(X.name,38,3))				AS unidade,
  	ifnull((substring_index(E.text__256,'.',1) * 1),0)	AS comp,
  	ifnull(substring_index(substring_index(E.text__256,'.', 2), '.', -1) * 1, 0) AS larg,
  	ifnull(substring_index(E.text__256,'.', -1)*1,0) 	AS alt,
  	L.localizacao						AS localizacao,
  	substring_index(L.localizacao,'.',1)			AS abreviacao
FROM sqldados.prdloc AS L
  INNER JOIN sqldados.prd AS X
      ON X.no = L.prdno
  LEFT JOIN sqldados.prdetq2 AS E
    ON  E.prdno = L.prdno
    AND E.seqno = 1
    AND E.text__256 regexp '^[0-9]+.[0-9]+.[0-9]+'
GROUP BY   L.storeno, L.prdno, L.grade, L.localizacao
HAVING L.localizacao <> ''

