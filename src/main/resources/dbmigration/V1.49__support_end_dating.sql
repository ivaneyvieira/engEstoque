-- apply changes
alter table notas
    add column lancamento_origem varchar(9) not null default 'EXPEDICAO';

