SELECT CAST(MD5(CAST(CONCAT(V.storeno, V.numero, E.storeno, E.numero) AS CHAR)) AS CHAR) AS id,
       V.storeno AS storenoVenda, V.numero AS numeroVenda, V.nfno AS nfnoVenda, V.nfse AS nfseVenda,
       V.date AS dataVenda, E.storeno AS storenoEntrega, E.numero AS numeroEntrega,
       E.nfno AS nfnoEntrega, E.nfse AS nfseEntrega, E.date AS dataEntrega,
       E.nfekey AS nfekeyEntrega
FROM (SELECT P.storeno, P.eordno AS ordno,
             IFNULL(cast(CONCAT(P.nfno, '/', P.nfse) AS CHAR), '') AS numero, P.nfno, P.nfse,
             N.nfekey, P.store_from, P.date
      FROM sqlpdv.pxa           AS P
        LEFT JOIN sqlpdv.pxanf2 AS N
                    USING (storeno, pdvno, xano)
      WHERE cfo IN (5922, 6922) AND date > :data_inicial)         AS V
  INNER JOIN (SELECT P.storeno, P.eordno AS ordno,
                     IFNULL(cast(CONCAT(P.nfno, '/', P.nfse) AS CHAR), '') AS numero, P.nfno,
                     P.nfse, N.nfekey, P.store_from, P.date
              FROM sqlpdv.pxa           AS P
                LEFT JOIN sqlpdv.pxanf2 AS N
                            USING (storeno, pdvno, xano)
              WHERE cfo IN (5117, 6117) AND date > :data_inicial) AS E
               USING (storeno, ordno)
WHERE V.storeno = 4 OR E.store_from = 4
GROUP BY storenoVenda, numeroVenda, storenoEntrega, numeroEntrega

