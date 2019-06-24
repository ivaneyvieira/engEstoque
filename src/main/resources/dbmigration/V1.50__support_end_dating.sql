-- apply changes
create table hitorico_etiquetas (
  id                            bigint auto_increment not null,
  usuario_id                    bigint not null,
  produto_id                    bigint not null,
  data                          date not null,
  hora                          time not null,
  print                         longtext not null,
  created_at                    datetime(6) not null,
  updated_at                    datetime(6) not null,
  version                       integer not null,
  constraint pk_hitorico_etiquetas primary key (id)
);

create index ix_hitorico_etiquetas_usuario_id on hitorico_etiquetas (usuario_id);
alter table hitorico_etiquetas add constraint fk_hitorico_etiquetas_usuario_id foreign key (usuario_id) references usuarios (id) on delete restrict on update restrict;

create index ix_hitorico_etiquetas_produto_id on hitorico_etiquetas (produto_id);
alter table hitorico_etiquetas add constraint fk_hitorico_etiquetas_produto_id foreign key (produto_id) references produtos (id) on delete restrict on update restrict;

