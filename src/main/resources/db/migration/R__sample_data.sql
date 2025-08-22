insert into app_user (user_id, version, username, display_name)
select 1,
       1,
       'admin',
       'Temp Admin' where not exists (select * from app_user);

insert into app_user_principal (user_id, encoded_password, enabled, admin)
select 1,
       '{noop}changeme',
       true,
       true where exists (select * from app_user where user_id = 1);
