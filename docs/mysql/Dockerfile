FROM mysql:8.4.2

COPY mysql.cnf /etc/mysql/mysql.conf.d/mysql.cnf
COPY init.sql /docker-entrypoint-initdb.d/init.sql
