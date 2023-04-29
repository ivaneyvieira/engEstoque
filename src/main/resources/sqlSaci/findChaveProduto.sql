SELECT nf.storeno, nf.nfno, nf.nfse
FROM sqldados.nf
         INNER JOIN TempNotaSaida
                    USING (storeno, nfno, nfse)