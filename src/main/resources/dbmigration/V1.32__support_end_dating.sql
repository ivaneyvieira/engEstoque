-- apply changes
alter table etiquetas
    add column status_nota varchar(9);
alter table etiquetas
    add constraint ck_etiquetas_status_nota check ( status_nota in ('RECEBIDO', 'INCLUIDA', 'CONFERIDA', 'ENTREGUE'));

