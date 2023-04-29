-- apply changes
alter table lojas
    add column sigla varchar(2) not null;

update lojas AS L
    inner join sqldados.store AS S
    ON S.no = L.numero
set L.sigla = S.sname;