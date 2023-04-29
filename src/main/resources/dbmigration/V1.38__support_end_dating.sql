-- apply changes
alter table notas
    modify tipo_nota varchar(17);
alter table notas
    add constraint ck_notas_tipo_nota check ( tipo_nota in
                                              ('COMPRA', 'TRANSFERENCIA_E', 'DEV_CLI', 'ACERTO_E', 'PEDIDO_E',
                                               'OUTROS_E', 'RECLASSIFICACAO_E', 'VENDA', 'TRANSFERENCIA_S', 'ENT_RET',
                                               'DEV_FOR', 'ACERTO_S', 'PEDIDO_S', 'OUTROS_S', 'OUTRAS_NFS', 'SP_REME'));
alter table notas
    add column codigo_barras varchar(60);

