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

-------------
-- Locations
-------------

create type location_type as enum ('REGIONAL_HQ', 'BRANCH_OFFICE', 'REMOTE_HUB', 'GLOBAL_HQ');

create type facility_type as enum ('FLOOR_SPACE', 'HOT_DESKS', 'KITCHEN', 'MEETING_BOOTHS', 'ACCESSIBLE_OFFICE', 'PARKING_SLOTS');

create sequence location_id_seq start 100 increment 1;

create table location
(
    location_id   bigint        not null,
    version       bigint        not null,
    name          text          not null,
    location_type location_type not null,
    time_zone     varchar(64)   not null,
    country       varchar(3)    not null,
    address       json          not null,
    about         text          not null,
    established   date          not null,
    primary key (location_id)
);

create table location_facility
(
    location_id   bigint        not null,
    facility_type facility_type not null,
    quantity      int           not null default 0,
    primary key (location_id, facility_type),
    foreign key (location_id) references location (location_id),
    constraint quantity_non_negative check (quantity >= 0)
);

-------------
-- Employees
-------------

create sequence employee_id_seq start 100 increment 1;

create type gender as enum ('FEMALE', 'MALE', 'OTHER');
create type employment_type as enum ('FULL_TIME', 'PART_TIME');
create type employment_status as enum ('ACTIVE', 'INACTIVE', 'TERMINATED');
create type work_arrangement as enum ('ONSITE', 'REMOTE', 'HYBRID');

create table employee
(
    employee_id    bigint       not null,
    version        bigint       not null,
    first_name     text         not null,
    middle_name    text,
    last_name      text         not null,
    preferred_name text         not null,
    birth_date     date         not null,
    gender         gender       not null,
    dietary_notes  text,
    country        varchar(3)   not null,
    time_zone      varchar(64)  not null,
    home_address   json,
    work_phone     varchar(16),
    mobile_phone   varchar(16),
    home_phone     varchar(16),
    work_email     varchar(320) not null,
    primary key (employee_id)
);

create table employment
(
    employee_id       bigint            not null,
    version           bigint            not null,
    job_title         text              not null,
    employment_type   employment_type   not null,
    employment_status employment_status not null,
    work_arrangement  work_arrangement  not null,
    location_id       bigint            not null,
    hire_date         date              not null,
    termination_date  date,
    primary key (employee_id),
    foreign key (employee_id) references employee (employee_id),
    foreign key (location_id) references location (location_id),
    constraint termination_date_matches_status check ((termination_date is null and not employment_status = 'TERMINATED') or
                                                      (termination_date is not null and employment_status = 'TERMINATED'))
);

---------
-- Teams
---------

create sequence team_id_seq start 100 increment 1;

create table team
(
    team_id             bigint not null,
    version             bigint not null,
    name                text   not null,
    summary             text,
    manager_employee_id bigint,
    primary key (team_id),
    foreign key (manager_employee_id) references employee (employee_id)
);

create table team_membership
(
    employee_id bigint not null,
    version     bigint not null,
    team_id     bigint not null,
    since       date   not null,
    primary key (employee_id),
    foreign key (employee_id) references employee (employee_id),
    foreign key (team_id) references team (team_id)
);

create table team_membership_history
(
    employee_id bigint not null,
    version     bigint not null,
    team_id     bigint not null,
    since       date   not null,
    until       date   not null,
    primary key (employee_id, version),
    foreign key (employee_id) references employee (employee_id),
    foreign key (team_id) references team (team_id)
);

------------
-- Projects
------------

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
