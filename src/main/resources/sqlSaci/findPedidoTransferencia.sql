SELECT DISTINCT cast(MD5(
        CONCAT(N.storeno, N.ordno, IFNULL(MID(L.localizacao, 1, 4), ''))) AS CHAR) AS id,
                R.no AS rota, N.storeno, N.ordno AS numero, N.date, C.name AS clienteName,
                IFNULL(MID(L.localizacao, 1, 4), '') AS abreviacao, nf.nfno, nf.nfse, N.status
FROM sqldados.eord           AS N
  INNER JOIN sqldados.eoprd  AS P
               USING (storeno, ordno)
  INNER JOIN sqldados.prdloc AS L
               USING (prdno, grade)
  INNER JOIN sqldados.custp  AS C
               ON C.no = N.custno
  INNER JOIN sqldados.store  AS S
               ON C.cpf_cgc = S.cgc
  INNER JOIN sqldados.xroute AS R
               ON R.storenoFrom = N.storeno AND R.storenoTo = S.no
  LEFT JOIN  sqldados.nf
               ON N.storeno = nf.storeno AND N.ordno = nf.eordno
WHERE (N.storeno = 4) AND (N.status = 2 OR nf.nfno IS NOT NULL) AND (localizacao <> '') AND
      N.date > :data_inicial