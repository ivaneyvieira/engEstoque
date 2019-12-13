DROP TABLE IF EXISTS tab_produtos ;
CREATE TABLE tab_produtos (PRIMARY KEY (produto_id))
select * from view_produtos;

DROP TABLE IF EXISTS t_loc_produtos ;
CREATE TABLE t_loc_produtos (PRIMARY KEY (id))
select * from v_loc_produtos;

DROP TABLE IF EXISTS tab_produtos_saci ;
CREATE TABLE tab_produtos_saci (PRIMARY KEY (id), index (codigo, grade))
select * from view_produtos_saci;

DROP TABLE IF EXISTS tab_produtos_grade ;
CREATE TABLE tab_produtos_grade (PRIMARY KEY (prdno, grade))
select * from view_produtos_grade
group by
    prdno,
    grade;

create index i1 on tab_produtos (codigo, grade) ; / /
create index i2 on tab_produtos (produto_id) ;
create index i1 on t_loc_produtos (codigo, grade) ;
create index i2 on t_loc_produtos (produto_id) ; / /
create index i1 on tab_produtos_saci (codigo, grade) ; / /
create index i1 on tab_produtos_grade (prdno, grade) ;
