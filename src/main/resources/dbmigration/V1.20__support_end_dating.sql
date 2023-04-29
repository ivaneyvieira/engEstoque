-- apply changes
alter table itens_nota
    alter impresso set default 0;
alter table itens_nota
    modify impresso null;
