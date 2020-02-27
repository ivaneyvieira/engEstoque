-- apply changes
ALTER TABLE usuarios
  ADD COLUMN abastecimento TINYINT(1) DEFAULT 0 NOT NULL;

