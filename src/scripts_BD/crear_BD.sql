CREATE SCHEMA XPRESS AUTHORIZATION sa 

CREATE TABLE XPRESS.PERSONA (
    id_persona int not null,
    nombre varchar,
    direccion varchar,
    fecha_alta date,
    PRIMARY KEY (id_persona)
);

CREATE TABLE XPRESS.USUARIO (
	id_usuario int not null PRIMARY KEY,
	username varchar,
	password varchar,
	fecha_alta date,
	id_persona int not null FOREIGN KEY REFERENCES XPRESS.PERSONA (id_persona)
);

CREATE TABLE XPRESS.ROL(
	id_rol int not null PRIMARY KEY,
	descripcion varchar
);

CREATE TABLE XPRESS.USUARIO_ROL ( 
	id_usuario_rol int not null PRIMARY KEY,
	id_usuario int not null FOREIGN KEY REFERENCES XPRESS.USUARIO (id_usuario),
	id_rol int not null FOREIGN KEY REFERENCES XPRESS.ROL (id_rol)
);

CREATE TABLE XPRESS.APLICACION ( 
	id_aplicacion int not null PRIMARY KEY,
	descripcion varchar
);

CREATE TABLE XPRESS.ROL_APLICACION ( 
	id_rol_aplicacion int not null PRIMARY KEY,
	id_rol int not null FOREIGN KEY REFERENCES XPRESS.ROL (id_rol),
	id_aplicacion int not null FOREIGN KEY REFERENCES XPRESS.APLICACION (id_aplicacion),
);
