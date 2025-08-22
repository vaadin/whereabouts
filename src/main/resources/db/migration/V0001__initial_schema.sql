create sequence app_user_id_seq start 100 increment 50;

create table app_user
(
    user_id      bigint not null primary key,
    version      bigint not null,
    username     text   not null unique,
    display_name text   not null
);

create table app_user_principal
(
    user_id          bigint  not null,
    encoded_password text,
    enabled          boolean not null default false,
    admin            boolean not null default false,
    primary key (user_id),
    foreign key (user_id) references app_user (user_id)
);

create sequence project_id_seq start 100 increment 50;

create table project
(
    project_id bigint not null primary key,
    name       text   not null
);

create sequence task_id_seq start 100 increment 50;

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
    task_id bigint not null,
    user_id bigint not null,
    primary key (task_id, user_id),
    foreign key (task_id) references task (task_id),
    foreign key (user_id) references app_user (user_id)
);
