CREATE OR REPLACE VIEW v_nota_futura AS
SELECT MAX(`I`.`id`)                              AS `id`,
       `I`.`nota_id`                              AS `nota_id`,
       `N`.`numero`                               AS `numero`,
       `N`.`tipo_mov`                             AS `tipo_mov`,
       `N`.`tipo_nota`                            AS `tipo_nota`,
       `N`.`rota`                                 AS `rota`,
       `N`.`data`                                 AS `data`,
       `N`.`hora`                                 AS `hora`,
       `N`.`observacao`                           AS `observacao`,
       `N`.`loja_id`                              AS `loja_id`,
       `N`.`created_at`                           AS `created_at`,
       `N`.`updated_at`                           AS `updated_at`,
       `N`.`version`                              AS `version`,
       `N`.`fornecedor`                           AS `fornecedor`,
       `N`.`cliente`                              AS `cliente`,
       `N`.`data_emissao`                         AS `data_emissao`,
       `N`.`sequencia`                            AS `sequencia`,
       `N`.`usuario_id`                           AS `usuario_id`,
       `N`.`lancamento`                           AS `lancamento`,
       substring_index(`I`.`localizacao`, '.', 1) AS `abreviacao`
FROM (`engEstoque`.`notas` `N`
    JOIN `engEstoque`.`itens_nota` `I`
      ON ((`I`.`nota_id` = `N`.`id`)))
WHERE (`I`.`status` = 'INCLUIDAF')
GROUP BY `N`.`id`,
         `abreviacao`;
