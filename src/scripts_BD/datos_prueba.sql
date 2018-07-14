insert into "XPRESS"."PERSONA" ("ID_PERSONA", "NOMBRE", "DIRECCION", "FECHA_ALTA") values(1, 'nico', 'nico@gmail.com', '2018-04-16');
insert into "XPRESS"."PERSONA" ("ID_PERSONA", "NOMBRE", "DIRECCION", "FECHA_ALTA") values(2, 'gaston', 'g_cast@gmail.com', '2018-03-05');
insert into "XPRESS"."PERSONA" ("ID_PERSONA", "NOMBRE", "DIRECCION", "FECHA_ALTA") values(3, 'cris', 'cris-rojas@yahoo.com', '2017-11-03');
insert into "XPRESS"."PERSONA" ("ID_PERSONA", "NOMBRE", "DIRECCION", "FECHA_ALTA") values(4, 'admin', 'admin@xpress.com', '2017-11-03');

insert into "XPRESS"."USUARIO" ("ID_USUARIO", "USERNAME", "PASSWORD", "ID_PERSONA") values(1, 'nikodmb', '1234',1);
insert into "XPRESS"."USUARIO" ("ID_USUARIO", "USERNAME", "PASSWORD", "ID_PERSONA") values(2, 'sa', 'ayed2018',4);
insert into "XPRESS"."USUARIO" ("ID_USUARIO", "USERNAME", "PASSWORD", "ID_PERSONA") values(3, 'gcast', '4321',2);
insert into "XPRESS"."USUARIO" ("ID_USUARIO", "USERNAME", "PASSWORD", "ID_PERSONA") values(4, 'crojas', 'contrasenia',3);

insert into "XPRESS"."ROL" ("ID_ROL", "DESCRIPCION") values (1,'Control Total');
insert into "XPRESS"."ROL" ("ID_ROL", "DESCRIPCION") values (2,'Gestor');
insert into "XPRESS"."ROL" ("ID_ROL", "DESCRIPCION") values (3,'Administrador');



insert into "XPRESS"."USUARIO_ROL" ("ID_USUARIO_ROL", "ID_USUARIO", "ID_ROL") values (1,1,1);
insert into "XPRESS"."USUARIO_ROL" ("ID_USUARIO_ROL", "ID_USUARIO", "ID_ROL") values (2,2,2);
insert into "XPRESS"."USUARIO_ROL" ("ID_USUARIO_ROL", "ID_USUARIO", "ID_ROL") values (3,3,3);
