SELECT DISTINCT
       MD5(CONCAT(N.storeno, N.ordno, SUBSTRING_INDEX(L.localizacao, '.', 1))) AS id,
       R.no                                   AS rota,
       N.storeno,
       N.ordno                                AS numero,
       N.date,
       C.name                                 AS clienteName,
       SUBSTRING_INDEX(L.localizacao, '.', 1) AS abreviacao,
       nf.nfno,
       nf.nfse,
       N.status
FROM sqldados.eord                  AS N
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
WHERE N.storeno = :storeno AND
      (N.status = 2 OR nf.nfno IS NOT NULL) AND
      localizacao <> ''
ORDER BY N.date DESC
