-- apply changes
create index ix_notas_tipo_mov_tipo_nota_sequencia on notas (tipo_mov,tipo_nota,sequencia);
