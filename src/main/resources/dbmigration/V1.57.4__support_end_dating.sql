-- apply changes
alter table usuarios add column entrega_futura tinyint(1) default 0 not null;

