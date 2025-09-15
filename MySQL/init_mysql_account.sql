### master execute
CREATE USER 'writeuser'@'%' IDENTIFIED BY 'Kjlsadfjlkasfdj@987mn';
GRANT INSERT, DELETE, UPDATE, SELECT ON wgdb.* TO 'writeuser'@'%';


### slave execute
CREATE USER 'readuser'@'%' IDENTIFIED BY 'Dfsafa@%dgfgu97';
GRANT SELECT ON wgdb.* TO 'readuser'@'%';