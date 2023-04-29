-- apply changes
create index ix_itens_nota_produto_id_nota_id on itens_nota (produto_id, nota_id);
create index ix_notas_tipo_mov_tipo_nota_sequencia on notas (tipo_mov, tipo_nota, sequencia);
