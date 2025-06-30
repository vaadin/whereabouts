create sequence project_id_seq increment 50;

create table project
(
    project_id bigint not null primary key,
    name       text   not null
);

create sequence task_id_seq increment 50;

create table task
(
    task_id       bigint       not null primary key,
    version       bigint       not null,
    project_id    bigint       not null,
    description   text         not null,
    due_date      date,
    due_time      time,
    time_zone     varchar(100) not null,
    due_date_time timestamp with time zone,
    task_status   int          not null,
    task_priority int          not null,
    foreign key (project_id) references project (project_id)
);

create table task_assignee
(
    task_id  bigint      not null,
    assignee varchar(36) not null,
    primary key (task_id, assignee),
    foreign key (task_id) references task (task_id)
);
