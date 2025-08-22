insert into app_user (user_id, version, username, display_name)
select 1,
       1,
       'admin',
       'Temp Admin' where not exists (select * from app_user);

insert into app_user_principal (user_id, encoded_password, enabled)
select 1,
       '{noop}changeme',
       true where exists (select * from app_user where user_id = 1);

insert into app_user_role (user_id, role_name)
select 1, 'ADMIN' where exists (select * from app_user where user_id = 1);

insert into app_user_role (user_id, role_name)
select 1, 'USER' where exists (select * from app_user where user_id = 1);
