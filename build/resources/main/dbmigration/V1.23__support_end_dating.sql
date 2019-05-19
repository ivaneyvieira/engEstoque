-- apply changes
alter table usuarios add column admin tinyint(1) default 0 not null;

