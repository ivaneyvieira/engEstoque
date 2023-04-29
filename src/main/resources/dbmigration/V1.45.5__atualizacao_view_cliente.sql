CREATE OR REPLACE VIEW v_codigo_barra_cliente AS
SELECT `N`.`id`        AS `id_nota`,
       REPLACE
           (
               concat(`L`.`numero`, ' ', `N`.`numero`, ' ', `N`.`sequencia`),
               '/',
               ' '
           )
                       AS `codbar`,
       REPLACE
           (
               concat(`L`.`numero`, ' ', `N`.`numero`),
               '/',
               ' '
           )
                       AS `codbar_nota`,
       REPLACE
           (
               concat(`L`.`numero`, `N`.`numero`, `N`.`sequencia`),
               '/',
               ''
           )
                       AS `codbar_limpo`,
       `L`.`numero`    AS `storeno`,
       `N`.`numero`    AS `numero`,
       `N`.`sequencia` AS `sequencia`
FROM (`engEstoque`.`notas` `N`
    JOIN `engEstoque`.`lojas` `L`
      ON ((`N`.`loja_id` = `L`.`id`)));