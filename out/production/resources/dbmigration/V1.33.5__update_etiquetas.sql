UPDATE etiquetas
SET status_nota = 'RECEBIDO'
WHERE titulo like 'Entrada%';

UPDATE etiquetas
SET status_nota = 'ENTREGUE'
WHERE titulo like 'Saída%';