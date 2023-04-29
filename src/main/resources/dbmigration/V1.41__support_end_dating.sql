-- apply changes
alter table itens_nota
    add column codigo_barra_conferencia varchar(60);
alter table itens_nota
    add column codigo_barra_entrega varchar(60);

