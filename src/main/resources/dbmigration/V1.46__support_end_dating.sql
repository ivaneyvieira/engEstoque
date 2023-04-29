-- apply changes
alter table usuarios
    add column trava_etiqueta tinyint(1) default 0 not null;

