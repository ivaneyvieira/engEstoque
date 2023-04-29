-- apply changes
alter table usuarios
    add column painel tinyint(1) default 0 not null;
alter table usuarios
    add column configuracao tinyint(1) default 0 not null;

