CREATE TABLE auditoria
(
    id_auditoria  SERIAL PRIMARY KEY NOT NULL,
    correo_usuario VARCHAR(255),
    rol            VARCHAR(255),
    accion         VARCHAR(255),
    fecha          TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE departamentos
(
    id_departamento SERIAL PRIMARY KEY NOT NULL,
    nombre_departamento VARCHAR(255)
);

CREATE TABLE ciudades
(
    id_ciudad SERIAL PRIMARY KEY NOT NULL,
    nombre_ciudad VARCHAR(255),
    id_departamento BIGINT
);


CREATE TABLE credenciales
(
    id_credencial SERIAL PRIMARY KEY NOT NULL,
    correo        VARCHAR(255),
    contrasena    VARCHAR(255)
);

CREATE TABLE modo_sistema
(
    id_modo_sistema SERIAL PRIMARY KEY NOT NULL,
    modo            BOOLEAN
);

CREATE TABLE personas
(
    id_persona                 SERIAL PRIMARY KEY NOT NULL,
    primer_nombre              VARCHAR(255),
    segundo_nombre             VARCHAR(255),
    primer_apellido            VARCHAR(255),
    segundo_apellido           VARCHAR(255),
    numero_identificacion      VARCHAR(255) UNIQUE NOT NULL,
    telefono                   VARCHAR(255),
    estado_votacion            BOOLEAN,
    id_puesto_votacion         BIGINT,
    id_mesa                    BIGINT,
    id_usuario                 BIGINT,
    fecha_registro             TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    imagen_url                 VARCHAR(255),
    year                       SMALLINT NOT NULL,
    numero_identificacion_hash VARCHAR(255),
    id_lider                   BIGINT
);

CREATE TABLE roles
(
    id_rol     SERIAL PRIMARY KEY NOT NULL,
    nombre_rol VARCHAR(255)
);

CREATE TABLE usuarios
(
    id_usuario    SERIAL PRIMARY KEY NOT NULL,
    nombre        VARCHAR(255),
    apellido      VARCHAR(255),
    estado        BOOLEAN,
    id_rol        BIGINT,
    id_credencial BIGINT,
    id_ciudad    BIGINT,
    id_lider     BIGINT
);

CREATE TABLE puesto_votacion (
    id_puesto_votacion SERIAL PRIMARY KEY NOT NULL,
    nombre_puesto VARCHAR(255),
    id_ciudad BIGINT
);

CREATE TABLE mesa (
    id_mesa SERIAL PRIMARY KEY NOT NULL,
    numero_mesa VARCHAR(255),
    id_puesto_votacion BIGINT
);



ALTER TABLE usuarios
    ADD CONSTRAINT uc_usuarios_id_credencial UNIQUE (id_credencial);

ALTER TABLE usuarios
    ADD CONSTRAINT FK_USUARIOS_ON_ID_CREDENCIAL FOREIGN KEY (id_credencial) REFERENCES credenciales (id_credencial);

ALTER TABLE usuarios
    ADD CONSTRAINT FK_USUARIOS_ON_ID_ROL FOREIGN KEY (id_rol) REFERENCES roles (id_rol);

ALTER TABLE ciudades
     ADD CONSTRAINT FK_CIUDAD_ON_ID_DEPARTAMENTO FOREIGN KEY (id_departamento) REFERENCES departamentos (id_departamento);

ALTER TABLE personas
    ADD CONSTRAINT FK_PERSONAS_ON_ID_USUARIO FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario);


ALTER TABLE usuarios
    ADD CONSTRAINT FK_USUARIOS_ON_ID_CIUDAD
        FOREIGN KEY (id_ciudad)
            REFERENCES ciudades (id_ciudad);

ALTER TABLE usuarios
    ADD CONSTRAINT FK_USUARIOS_ON_ID_LIDER
        FOREIGN KEY (id_lider)
            REFERENCES usuarios (id_usuario);


ALTER TABLE personas
    ADD CONSTRAINT FK_PERSONAS_ON_ID_LIDER
        FOREIGN KEY (id_lider)
            REFERENCES usuarios (id_usuario);


ALTER TABLE puesto_votacion
    ADD CONSTRAINT FK_PUESTO_VOTACION_ON_ID_CIUDAD
        FOREIGN KEY (id_ciudad)
            REFERENCES ciudades (id_ciudad);


ALTER TABLE mesa
    ADD CONSTRAINT FK_MESA_ON_ID_PUESTO_VOTACION
        FOREIGN KEY (id_puesto_votacion)
            REFERENCES puesto_votacion (id_puesto_votacion);

