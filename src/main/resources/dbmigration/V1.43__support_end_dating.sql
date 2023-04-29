-- apply changes
alter table etiquetas
    add column etiqueta_default tinyint(1) default 0 not null;

