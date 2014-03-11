create table master (major integer not null, minor bigint not null, name varchar(20))

alter table master add primary key (major, minor)

create table detail (id integer primary key, master_major integer, master_minor bigint, name varchar(30))

alter table detail add foreign key (master_major, master_minor) references master (major, minor)
