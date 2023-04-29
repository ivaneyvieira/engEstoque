-- apply changes
alter table usuarios
    add column tipo_usuario varchar(9) not null;
alter table usuarios
    add constraint ck_usuarios_tipo_usuario check ( tipo_usuario in ('ESTOQUE', 'EXPEDICAO'));

