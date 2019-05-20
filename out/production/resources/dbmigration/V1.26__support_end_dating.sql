-- apply changes
alter table itens_nota drop index uq_itens_nota_nota_id_produto_id;
alter table itens_nota add constraint uq_itens_nota_nota_id_produto_id_localizacao unique  (nota_id,produto_id,localizacao);
