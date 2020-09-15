SELECT N.lojaVenda                                                                        AS LJ,
       RIGHT(N.prdno, 6)                                                                  AS PRODUTO,
       LPAD(P.name, 27, ' ')                                                              AS DESCRICAO,
       RIGHT(RPAD(P.name, 40, ' '), 3)                                                    AS UN,
       LPAD(N.grade, 9, ' ')                                                              AS GRADE,
       LPAD(S.ncm, 12, ' ')                                                               AS NCM,
       LPAD(P.mfno, 7, ' ')                                                               AS COD_FAB,
       RPAD(V.sname, 4, ' ')                                                              AS FABR,
       V.state                                                                            AS UF,
       LPAD(P.free_fld1, 3, ' ')                                                          AS ST,
       CONCAT(nfCompra, '/', seCompra)                                                    AS NF_ENT,
       date_format(dataCompra, '%d/%m/%Y')                                                AS D_EMISSAO,
       date_format(R.date, '%d/%m/%Y')                                                    AS D_ENTRADA,
       sqldados.moneyFormat(IFNULL(valorCompra, 0))                                       AS V_N_COMPRA,
       LPAD(CONCAT(nfVenda, '/', seVenda), 10, ' ')                                       AS NF_VENDA,
       DATE_FORMAT(dataVenda, '%d/%m/%Y')                                                 AS D_VENDA,
       DATE_FORMAT(dataVenda, '%d/%m/%Y')                                                 AS D_SAIDA,
       sqldados.moneyFormat(valorVenda)                                                   AS V_NF_VENDA,
       sqldados.moneyFormat(quantidade_compra)                                            AS QUANT,
       sqldados.moneyFormat(precoUnitarioVenda)                                           AS V_T_I_VENDA,
       LPAD(P.taxno, 3, ' ')                                                              AS CST,
       sqldados.moneyFormat(P.auxShort1 / 10000)                                          AS ICMS,
       sqldados.moneyFormat(P.lucroTributado / 10000)                                     AS MVA,
       sqldados.moneyFormat(quantidade_compraTotal)                                       AS QUANT_ENT,
       sqldados.moneyFormat(precoUnitarioCompra)                                          AS V_I_ENT,
       sqldados.moneyFormat(
         quantidade_compraTotal * precoUnitarioCompra)                                    AS V_T_I_ENT,
       sqldados.moneyFormat(
         IFNULL(precoUnitarioCompra * quantidade_compraTotal * (R.ipi / (100 * 100)),
                0))                                                                       AS 'V_IPI',
       sqldados.moneyFormat(IFNULL(precoUnitarioCompra * quantidade_compraTotal +
                                   precoUnitarioCompra * quantidade_compraTotal *
                                   (R.ipi / (100 * 100)) * (1 + P.lucroTributado / (100 * 100)),
                                   0))                                                    AS B_ST,
       sqldados.moneyFormat(R.ipi / (100 * 100))                                          AS P_IPI
FROM planilhaConsultoria     AS N
  LEFT JOIN sqldados.iprd    AS R
              ON R.invno = N.invno AND R.prdno = N.prdno AND R.grade = N.grade
  LEFT JOIN sqldados.prd     AS P
              ON (P.no = N.prdno)
  LEFT JOIN sqldados.spedprd AS S
              ON (S.prdno = N.prdno)
  LEFT JOIN sqldados.vend    AS V
              ON (V.no = P.mfno)
GROUP BY N.lojaVenda,
         N.nfVenda,
         N.seVenda,
         N.lojaCompra,
         N.nfCompra,
         N.seCompra,
         N.prdno,
         N.grade;
