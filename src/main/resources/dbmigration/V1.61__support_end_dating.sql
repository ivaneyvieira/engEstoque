-- apply changes
alter table notas
    add column numero_entrega varchar(40) not null default "";

create index ix_notas_numero_entrega on notas (numero_entrega);
