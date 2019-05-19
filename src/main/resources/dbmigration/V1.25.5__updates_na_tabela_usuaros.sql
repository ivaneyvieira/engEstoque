UPDATE usuarios
set admin = 1
where login_name in ('YASMINE', 'ADM');

UPDATE usuarios
set loja_id = (select id from lojas where numero = 4);

