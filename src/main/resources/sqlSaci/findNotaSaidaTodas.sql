SELECT * FROM engEstoque.notas_saida
WHERE storeno = :storeno AND
      localizacao LIKE :abreviacao