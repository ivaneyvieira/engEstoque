SELECT A.nfstoreno AS storeno,
       A.nfpdvno   AS pdvno,
       A.nfxano    AS xano,
       A.invno,
       I.nfname    AS nfeno,
       I.invse     AS nfese,
       N.nfno      AS nfsno,
       N.nfse      AS nfsse
FROM iprdrm AS A
         INNER JOIN inv AS I
                    USING (invno)
         INNER JOIN nf AS N
                    ON N.storeno = A.nfstoreno AND N.pdvno = A.nfpdvno AND N.xano = A.nfxano AND
                       N.status != 1 AND I.bits & POW(2, 4) = 0
         INNER JOIN sqldados.prdloc AS L
                    ON L.storeno = A.nfstoreno AND L.prdno = A.prdno AND L.grade = A.grade
WHERE I.date > DATE_SUB(current_date, INTERVAL 30 DAY)
  AND A.nfstoreno = :storeno
  AND L.localizacao LIKE :abreviacao
GROUP BY A.nfstoreno, A.nfpdvno, A.nfxano, A.invno;