SELECT U.no,
       U.name,
       login,
       auxLong1                                                                           AS storeno,
       CAST(CONCAT(CHAR(ASCII(SUBSTRING(pswd, 1, 1)) + ASCII('e') - ASCII('j')),
		   CHAR(ASCII(SUBSTRING(pswd, 2, 1)) + ASCII('a') - ASCII('h')),
		   CHAR(ASCII(SUBSTRING(pswd, 3, 1)) + ASCII('c') - ASCII('k')),
		   CHAR(ASCII(SUBSTRING(pswd, 4, 1)) + ASCII(' ') - ASCII(' ')),
		   CHAR(ASCII(SUBSTRING(pswd, 5, 1)) + ASCII(' ') - ASCII('B')),
		   CHAR(ASCII(SUBSTRING(pswd, 6, 1)) + ASCII(' ') - ASCII(')')),
		   CHAR(ASCII(SUBSTRING(pswd, 7, 1)) + ASCII(' ') - ASCII(')')),
		   CHAR(ASCII(SUBSTRING(pswd, 8, 1)) + ASCII(' ') - ASCII('-'))) AS CHAR) AS senha,
       IFNULL(P.name, '')                                                                 AS impressora
FROM sqldados.users        AS U
  LEFT JOIN sqldados.prntr AS P
	      ON P.no = U.prntno
WHERE login = :login
   OR :login = 'TODOS'