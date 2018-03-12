drop database IF  EXISTS  slhinit;
CREATE DATABASE IF NOT EXISTS slhinit DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
use slhinit;
set global innodb_flush_log_at_trx_commit = 0;
source SLH_mysql_create-1.sql  
source SLHinitData-3.sql
set global innodb_flush_log_at_trx_commit = 1;   