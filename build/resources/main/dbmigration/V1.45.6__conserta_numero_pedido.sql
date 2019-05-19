update notas
set numero = TRIM(TRAILING '/' FROM numero)
where numero like '%/';