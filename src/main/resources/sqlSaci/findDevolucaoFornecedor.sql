select A.nfstoreno as storeno, A.nfpdvno as pdvno, A.nfxano as xano, A.invno, I.nfname as nfeno,
       I.invse as nfese, N.nfno AS nfsno, N.nfse AS nfsse
from iprdrm AS A
         inner join inv AS I
                    USING(invno)
         inner join nf AS N
                    ON  N.storeno = A.nfstoreno
                        AND N.pdvno   = A.nfpdvno
                        AND N.xano    = A.nfxano
                        AND N.status  != 1
                        AND I.bits & POW(2, 4) = 0
where I.date > DATE_SUB(current_date, interval 30 day)
        and A.nfstoreno = :storeno
group by A.nfstoreno, A.nfpdvno, A.nfxano, A.invno;