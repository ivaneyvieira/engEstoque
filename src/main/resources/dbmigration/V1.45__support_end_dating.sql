-- apply changes
alter table usuarios
    add column nota_series varchar(4000) not null;

