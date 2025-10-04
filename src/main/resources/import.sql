insert into tb_role(name) VALUES ('MEDICO');
insert into tb_role(name) VALUES ('ENFERMEIRO');
insert into tb_role(name) VALUES ('PACIENTE');

insert into tb_user ("username", "password", "login", "enabled") VALUES ('DR JOSE', '1234', 'drjose', true);
insert into tb_user ("username", "password", "login", "enabled") VALUES ('ENFERMEIRA MARIA', '9876', 'efmaria', true);
insert into tb_user ("username", "password", "login", "enabled") VALUES ('Jocileide Mariano da Silva', '4321', 'jocmasilva', true);

insert into user_roles (role_id, user_id) VALUES (1, 1);
insert into user_roles (role_id, user_id) VALUES (2, 2);
insert into user_roles (role_id, user_id) VALUES (3, 3);