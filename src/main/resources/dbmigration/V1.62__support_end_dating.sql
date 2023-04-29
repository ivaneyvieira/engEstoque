-- apply changes
alter table usuarios
    add column impressora varchar(40) not null default "";

