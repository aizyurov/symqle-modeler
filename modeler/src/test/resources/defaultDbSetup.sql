create table all_types (
  t_BIT bigint primary key,
  t_TINYINT smallint,
  t_SMALLINT smallint,
  t_MEDIUMINT integer,
  t_INTEGER integer,
  t_BIGINT bigint,
  t_FLOAT float(23),
  t_REAL real,
  t_DOUBLE double,
  t_NUMERIC numeric(10,3),
  t_DECIMAL decimal(10,3),
  t_CHAR char(10),
  t_VARCHAR varchar(20),
  t_LONGVARCHAR long varchar,
  t_DATE date,
  t_TIME time,
  t_TIMESTAMP timestamp,
  t_DATETIME timestamp,
  t_BINARY CHAR (4) FOR BIT DATA,
  t_VARBINARY VARCHAR (1000) FOR BIT DATA,
  t_LONGVARBINARY LONG VARCHAR FOR BIT DATA,
  t_BLOB blob,
  t_CLOB clob,
  t_BOOLEAN boolean,
  t_NCHAR char(10),
  t_NVARCHAR varchar(20),
  t_LONGNVARCHAR long varchar,
  t_NCLOB clob
)

create table department (dept_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, dept_name VARCHAR(100) NOT NULL, manager_id INTEGER, parent_dept INTEGER)

create table employee (emp_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, first_name VARCHAR(30), last_name VARCHAR(50), title VARCHAR(50),
  hire_date DATE, is_retired BOOLEAN default false, salary FLOAT, dept_id INTEGER)

alter table department add constraint PARENT_FK foreign key (parent_dept) references department

alter table department add constraint MANAGER_FK foreign key (manager_id) references employee

alter table employee add constraint DEPARTMENT_FK foreign key (dept_id) references department

create table master (major integer not null, minor bigint not null, name varchar(20))

alter table master add primary key (major, minor)

create table detail (id integer primary key, master_major integer, master_minor bigint, name varchar(30))

alter table detail add constraint MASTER_FK foreign key (master_major, master_minor) references master (major, minor)

create table plain (message varchar (100), message_ varchar(50))

create table plain_ (message varchar (100), fk integer,
constraint PLAIN_EMPLOYEE_FK foreign key (fk) references employee,
employee_id integer,
constraint PLAIN_EMPLOYEE_FK2 foreign key (employee_id) references employee,
employee_fk integer,
constraint PLAIN_EMPLOYEE_FK3 foreign key (employee_fk) references employee
)

create table first1 (first_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, text varchar(255))

create table second1 (first_id INTEGER PRIMARY KEY, text varchar(255),
                 constraint first1fk foreign key(first_id) references first1)

create table third2 (third_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, text varchar(255))

create table second2 (third_id INTEGER PRIMARY KEY, text varchar(255),
                 constraint third2fk foreign key(third_id) references third2)

create table first2(second_id INTEGER PRIMARY KEY, text varchar(255),
                 constraint second2fk foreign key(second_id) references second2)
