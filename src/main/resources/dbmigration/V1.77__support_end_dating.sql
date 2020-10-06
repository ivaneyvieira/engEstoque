-- apply changes
create index ix_produtos_id_loja_id on produtos (id,loja_id);
