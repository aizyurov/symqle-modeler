drop table if exists ALL_TYPES

create table ALL_TYPES (
  T_BIT bit(2) PRIMARY KEY,
  T_TINYINT tinyint,
  T_SMALLINT smallint,
  T_MEDIUMINT mediumint,
  T_INTEGER integer,
  T_BIGINT bigint,
  T_FLOAT float(23),
  T_REAL real,
  T_DOUBLE double,
  T_NUMERIC numeric(10,3),
  T_DECIMAL decimal(10,3),
  T_CHAR char(10),
  T_VARCHAR varchar(20),
  T_LONGVARCHAR tinytext,
  T_DATE date,
  T_TIME time,
  T_TIMESTAMP timestamp,
  T_DATETIME timestamp,
  T_BINARY binary (4),
  T_VARBINARY varbinary (1000),
  T_LONGVARBINARY tinyblob,
  T_BLOB blob,
  T_CLOB text,
  T_BOOLEAN boolean,
  T_NCHAR char(10),
  T_NVARCHAR varchar(20),
  T_LONGNVARCHAR tinytext,
  T_NCLOB text
);

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

drop table if exists EMPLOYEE

drop table if exists  DEPARTMENT

drop table if exists DETAIL

drop table if exists  MASTER

/*!40014 SET FOREIGN_KEY_CHECKS=1 */;

create table DEPARTMENT (DEPT_ID INTEGER PRIMARY KEY AUTO_INCREMENT, DEPT_NAME VARCHAR(100) NOT NULL, MANAGER_ID INTEGER, PARENT_DEPT INTEGER)

create table EMPLOYEE (EMP_ID INTEGER PRIMARY KEY AUTO_INCREMENT, FIRST_NAME VARCHAR(30), LAST_NAME VARCHAR(50), TITLE VARCHAR(50),
  HIRE_DATE DATE, IS_RETIRED BOOLEAN default false, SALARY FLOAT, DEPT_ID INTEGER)

alter table DEPARTMENT add constraint PARENT_FK foreign key (PARENT_DEPT) references DEPARTMENT (DEPT_ID)

alter table DEPARTMENT add constraint MANAGER_FK foreign key (MANAGER_ID) references EMPLOYEE (EMP_ID)

alter table EMPLOYEE add constraint DEPARTMENT_FK foreign key (DEPT_ID) references DEPARTMENT (DEPT_ID)

create table MASTER (MAJOR integer not null, MINOR bigint not null, NAME varchar(20))

alter table MASTER add primary key (MAJOR, MINOR)

create table DETAIL (ID integer primary key, MASTER_MAJOR integer, MASTER_MINOR bigint, name varchar(30))

alter table DETAIL add constraint MASTER_FK foreign key (MASTER_MAJOR, MASTER_MINOR) references MASTER (MAJOR, MINOR)

