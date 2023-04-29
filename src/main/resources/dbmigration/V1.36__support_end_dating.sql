-- apply changes
alter table usuarios
    add column estoque tinyint(1) default 0 not null;
alter table usuarios
    add column expedicao tinyint(1) default 0 not null;

