-- apply changes
create table validades
(
    id               bigint auto_increment not null,
    meses_validade   integer               not null,
    meses_fabricacao integer               not null,
    created_at       datetime(6)           not null,
    updated_at       datetime(6)           not null,
    version          integer               not null,
    constraint uq_validades_meses_validade_meses_fabricacao unique (meses_validade, meses_fabricacao),
    constraint pk_validades primary key (id)
);

alter table abreviacoes
    modify impressora varchar(30);
