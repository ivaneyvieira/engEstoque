-- apply changes
alter table notas add column usuario_id bigint;

create index ix_notas_usuario_id on notas (usuario_id);
alter table notas add constraint fk_notas_usuario_id foreign key (usuario_id) references usuarios (id) on delete restrict on update restrict;

