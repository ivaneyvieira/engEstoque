-- apply changes
alter table abreviacoes
    drop index uq_abreviacoes_abreviacao;
alter table abreviacoes
    add constraint uq_abreviacoes_loja_id_abreviacao unique (loja_id, abreviacao);
