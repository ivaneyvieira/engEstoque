-- apply changes
alter table hitorico_etiquetas
    add column gtin varchar(255) not null;
alter table hitorico_etiquetas
    add column gtin_ok tinyint(1) default 0 not null;

