-- apply changes
alter table notas add constraint ck_notas_tipo_nota check ( tipo_nota in ('COMPRA','TRANSFERENCIA_E','DEV_CLI','ACERTO_E','PEDIDO_E','OUTROS_E','VENDA','TRANSFERENCIA_S','ENT_RET','DEV_FOR','ACERTO_S','PEDIDO_S','OUTROS_S','OUTRAS_NFS','SP_REME'));
alter table notas add column status varchar(9) not null;
alter table notas add constraint ck_notas_status check ( status in ('INCLUIDA','CONFERIDA','ENTREGUE'));

--alter table produtos drop foreign key fk_produtos_id;
--alter table produtos add constraint fk_produtos_id foreign key (id) references tab_produtos (produto_id) on delete restrict on update restrict;
