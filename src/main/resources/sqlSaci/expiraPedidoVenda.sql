UPDATE sqldados.eord
SET status = 4
WHERE storeno = :storeno AND ordno = :ordno;

UPDATE sqldados.eoprd
SET status = 4
WHERE storeno = :storeno AND ordno = :ordno;

UPDATE sqldados.eoprd2
SET status = 4
WHERE storeno = :storeno AND ordno = :ordno
