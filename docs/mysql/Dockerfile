FROM mysql:9.0.1

COPY mysql.cnf /etc/mysql/mysql.conf.d/mysql.cnf
COPY init.sql /docker-entrypoint-initdb.d/init.sql
