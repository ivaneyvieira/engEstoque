SELECT DISTINCT CAST(IFNULL(X.xrouteno, '') AS CHAR) AS rota,
		N.storeno,
		N.nfno                               AS numero,
		N.nfse                               AS serie,
		N.issuedate                          AS date,
		N.issuedate                          AS dtEmissao,
		P.prdno                              AS prdno,
		P.grade,
		P.qtty                               AS quant,
		C.name                               AS clienteName,
		CASE
		  WHEN (N.nfse = 1 AND N.cfo IN (5922, 6922)) OR
		       (N.nfse = 7 AND N.cfo IN (5405, 6102))
		    THEN 'VENDAF'
		  WHEN N.nfse = '66'
		    THEN 'ACERTO_S'
		  WHEN N.nfse = '3'
		    THEN 'ENT_RET'
		  WHEN tipo = 0
		    THEN 'VENDA'
		  WHEN tipo = 1
		    THEN 'TRANSFERENCIA_S'
		  WHEN tipo = 2
		    THEN 'DEV_FOR'
		  WHEN tipo = 3
		    THEN 'OUTRAS_NFS'
		  WHEN tipo = 7
		    THEN 'OUTRAS_NFS'
		  ELSE 'SP_REME'
		END                                  AS tipo,
		''                                   AS abreviacao
FROM sqldados.nf             AS N
  INNER JOIN sqldados.xaprd  AS P
	       USING (storeno, pdvno, xano)
  INNER JOIN sqldados.prdloc AS E
	       ON E.prdno = P.prdno AND E.grade = P.grade AND E.storeno = 4
  LEFT JOIN  sqldados.custp  AS C
	       ON C.no = N.custno
  LEFT JOIN  sqldados.inv    AS I
	       ON N.invno = I.invno
  LEFT JOIN  sqldados.xfr    AS X
	       ON X.no = I.xfrno
WHERE N.storeno = :storeno
  AND N.nfno = :nfno
  AND N.nfse = :nfse
  AND N.status <> 1
HAVING tipo <> ''