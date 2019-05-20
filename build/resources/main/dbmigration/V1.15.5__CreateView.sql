create or replace view v_loc_produtos AS
select MD5(CONCAT(prdno, L.grade, storeno)) as 'id',
storeno, prdno as codigo, L.grade, localizacao,
  substring_index(localizacao, '.', 1) AS abreviacao,
  P.id as produto_id, E.id AS loja_id
from sqldados.prdloc AS L
  inner join engEstoque.produtos AS P
    ON P.codigo = L.prdno
    AND P.grade = L.grade
  inner join engEstoque.lojas AS E
    ON E.numero = L.storeno;
CREATE
  OR
REPLACE VIEW view_produtos AS
SELECT DISTINCT `S`.`prdno`         AS `codigo`,
  `S`.`grade`                   AS `grade`,
  TRIM(`S`.`barcode`)           AS `codebar`,
  TRIM(substr(`S`.`name`,1,37)) AS `nome`,
  (0 / 10000)                   AS `custo`,
  substr(`S`.`name`,38,3)       AS `unidade`,
  (
  CASE
    WHEN (`S`.`clno` BETWEEN 10000
    AND 19999)
    THEN 'CAIXA'
    WHEN (`S`.`clno` BETWEEN 120000
    AND 129999)
    THEN if((substr(`S`.`name`,38,3) LIKE 'MT%'),'BOBINA','PECA')
    ELSE 'NORMAL'
  END)                     AS `tipo`,
  `P`.`id`                      AS `produto_id`
FROM (`sqlpdv`.`prdstk` `S`
  JOIN `engEstoque`.`produtos` `P`
  ON(((`S`.`prdno` = `P`.`codigo`)
  AND (`S`.`grade` = `P`.`grade`))));
CREATE
  OR
REPLACE VIEW view_produtos_saci AS
SELECT DISTINCT TRIM(concat(lpad(TRIM(`S`.`prdno`),6,'0'),':',rpad(`S`.`grade`,8,'.'))) AS `id`,
  `S`.`prdno`                                                                       AS `codigo`,
  `S`.`grade`                                                                       AS `grade`,
  TRIM(`S`.`barcode`)                                                               AS `codebar`,
  TRIM(substr(`S`.`name`,1,37))                                                     AS `nome`,
  (0 / 10000)                                                                       AS `custo`,
  substr(`S`.`name`,38,3)                                                           AS `unidade`,
  (
  CASE
    WHEN (`S`.`clno` BETWEEN 10000
    AND 19999)
    THEN 'CAIXA'
    WHEN (`S`.`clno` BETWEEN 120000
    AND 129999)
    THEN if((substr(`S`.`name`,38,3) LIKE 'MT%'),'BOBINA','PECA')
    ELSE 'NORMAL'
  END)                                                                         AS `tipo`
FROM `sqlpdv`.`prdstk` `S`
WHERE (`S`.`storeno` = 4);
