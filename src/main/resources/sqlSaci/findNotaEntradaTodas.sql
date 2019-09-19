SELECT * FROM engEstoque.notas_entrada
WHERE storeno = :storeno AND
      localizacao LIKE :abreviacao