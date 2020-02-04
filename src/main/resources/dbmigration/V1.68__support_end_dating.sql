-- apply changes
ALTER TABLE usuarios
  ADD COLUMN ressuprimento TINYINT(1) DEFAULT 0 NOT NULL;

