-- apply changes

alter table usuarios
    modify localizacaoes varchar(4000) not null;

alter table notas
    add column fornecedor varchar(60) not null;
alter table notas
    add column cliente varchar(60) not null;

