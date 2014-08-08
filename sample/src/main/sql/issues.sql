/*!40014 SET FOREIGN_KEY_CHECKS=0 */
;

drop table if exists record cascade
;
drop table if exists  ticket cascade
;
drop table if exists user_project cascade
;
drop table if exists project cascade
;
drop table if exists users cascade
;


create table users(id bigint primary key auto_increment, name varchar(100) not null, email varchar(100), unique key (name))
;

create table project (id bigint primary key auto_increment, name varchar(100) not null, unique key (name))
;

create table user_project(user_id bigint not null, project_id bigint not null,
    constraint up_user_fk foreign key (user_id) references users(id),
    constraint up_project_fk foreign key (project_id) references project(id),
    primary key (user_id, project_id))
;

create table ticket (id bigint primary key auto_increment, name varchar(200), description varchar(2000),
    creationDate timestamp, author_id bigint, constraint tc_author_fk foreign key (author_id) references users(id),
    project_id bigint, constraint tc_project_fk foreign key (project_id) references project(id),
    last_record_id bigint)
;
create table record (id bigint primary key auto_increment,
    ticket_id bigint, constraint rec_ticket_fk foreign key (ticket_id) references ticket(id),
    comment varchar(2000), editor_id bigint not null, constraint rec_editor_fk foreign key (editor_id) references users(id),
    assignee_id bigint not null, constraint rec_assignee_fk foreign key (assignee_id) references users(id),
    active boolean not null default true)
;
alter table ticket add constraint last_record_fk foreign key (last_record_id) references record(id)
;

/*!40014 SET FOREIGN_KEY_CHECKS=1 */
;







