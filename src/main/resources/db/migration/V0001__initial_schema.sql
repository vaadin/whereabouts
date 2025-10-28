create sequence app_user_id_seq start 100 increment 1;

create table app_user
(
    user_id          bigint  not null,
    version      bigint not null,
    username     text   not null,
    encoded_password text,
    display_name text   not null,
    enabled          boolean not null default false,
    primary key (user_id),
    unique (username)
);

create table app_user_role
(
    user_id   bigint not null,
    role_name text   not null,
    primary key (user_id, role_name),
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
    user_id bigint,
    primary key (employee_id),
    unique (user_id),
    unique (work_email),
    foreign key (user_id) references app_user (user_id)
);

create index employee_user_id_idx on employee (user_id);
create index employee_user_first_name_idx on employee (first_name);
create index employee_user_middle_name_idx on employee (middle_name);
create index employee_user_last_name_idx on employee (last_name);

-- TODO Should 'employee' and 'employment_details' be a single table?

create table employment_details
(
    employee_id         bigint            not null,
    version             bigint            not null,
    job_title           text              not null,
    employment_type     employment_type   not null,
    employment_status   employment_status not null,
    work_arrangement    work_arrangement  not null,
    location_id         bigint            not null,
    manager_employee_id bigint,
    hire_date           date              not null,
    termination_date    date,
    primary key (employee_id),
    foreign key (employee_id) references employee (employee_id),
    foreign key (location_id) references location (location_id),
    foreign key (manager_employee_id) references employee (employee_id),
    constraint termination_date_matches_status check ((termination_date is null and not employment_status = 'TERMINATED') or
                                                      (termination_date is not null and employment_status = 'TERMINATED'))
);

create index employment_details_location_id_idx on employment_details (location_id);
create index employment_details_manager_employee_id_idx on employment_details (manager_employee_id);
create index employment_details_type_idx on employment_details (employment_type);
create index employment_details_status_idx on employment_details (employment_status);

------------
-- Projects
------------

create sequence project_id_seq start 100 increment 1;

create table project
(
    project_id  bigint not null,
    version     bigint not null,
    name        text   not null,
    description text,
    primary key (project_id)
);

create index project_name_idx on project (name);

create sequence task_id_seq start 100 increment 1;

create type task_priority as enum ('URGENT', 'HIGH', 'NORMAL', 'LOW');
create type task_status as enum ('PENDING', 'PLANNED', 'IN_PROGRESS', 'PAUSED', 'DONE');

create table task
(
    task_id       bigint        not null,
    version       bigint        not null,
    project_id    bigint        not null,
    description   text          not null,
    due_date      date,
    due_time      time,
    time_zone     varchar(64)   not null,
    due_date_time timestamp with time zone,
    task_status   task_status   not null,
    task_priority task_priority not null,
    primary key (task_id),
    foreign key (project_id) references project (project_id)
);

create index task_description_idx on task (description);
create index task_status_idx on task (task_status);
create index task_priority_idx on task (task_priority);

create index task_project_id_idx on task (project_id);

create table task_assignee
(
    task_id     bigint not null,
    employee_id bigint not null,
    primary key (task_id, employee_id),
    foreign key (task_id) references task (task_id),
    foreign key (employee_id) references employee (employee_id)
);
