-- apply changes
alter table usuarios
    add column etiqueta tinyint(1) default 0 not null;

