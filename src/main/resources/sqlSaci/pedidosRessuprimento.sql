SELECT DISTINCT ''                  AS rota,
                MID(N.no, 1, 1) * 1 AS storeno,
                N.no                AS numero,
                ''                  AS serie,
                N.date,
                N.date              AS dtEmissao,
                P.prdno,
                P.grade,
                ROUND(P.qtty, 2)    AS quant,
                IFNULL(V.name, '')  AS clienteName,
                'PEDIDO_R'          AS tipo,
                auxStr              AS abreviacao
FROM sqldados.ords AS N
         INNER JOIN sqldados.oprd AS P
                    ON P.storeno = N.storeno AND P.ordno = N.no
         LEFT JOIN sqldados.vend AS V
                   ON V.no = N.vendno
WHERE N.storeno = 1
  AND N.no = :ordno
  AND N.no BETWEEN 100000000 AND 999999999