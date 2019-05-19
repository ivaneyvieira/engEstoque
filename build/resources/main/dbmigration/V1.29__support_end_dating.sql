-- apply changes
alter table itens_nota add column status varchar(9) not null;
alter table itens_nota add constraint ck_itens_nota_status check ( status in ('RECEBIDO','INCLUIDA','CONFERIDA','ENTREGUE'));

