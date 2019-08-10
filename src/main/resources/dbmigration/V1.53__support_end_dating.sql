-- apply changes
create table abreviacoes (
  id                            bigint auto_increment not null,
  abreviacao                    varchar(6) not null,
  expedicao                     tinyint(1) default 0 not null,
  impressora                    varchar(15) not null,
  created_at                    datetime(6) not null,
  updated_at                    datetime(6) not null,
  version                       integer not null,
  constraint pk_abreviacoes primary key (id)
);

