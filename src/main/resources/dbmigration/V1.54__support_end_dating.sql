-- apply changes
alter table abreviacoes add constraint uq_abreviacoes_abreviacao unique  (abreviacao);
