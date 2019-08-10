-- apply changes
alter table abreviacoes add column loja_id bigint not null;

create index ix_abreviacoes_loja_id on abreviacoes (loja_id);
alter table abreviacoes add constraint fk_abreviacoes_loja_id foreign key (loja_id) references lojas (id) on delete restrict on update restrict;

