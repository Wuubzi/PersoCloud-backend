CREATE TABLE auditoria
(
    id_auditoria  SERIAL PRIMARY KEY NOT NULL,
    correo_usuario VARCHAR(255),
    rol            VARCHAR(255),
    accion         VARCHAR(255),
    fecha          TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE barrios
(
    id_barrio     SERIAL PRIMARY KEY NOT NULL,
    nombre_barrio VARCHAR(255),
    estado        BOOLEAN,
    id_usuario      BIGINT
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
    lugar_votacion             VARCHAR(255),
    id_barrio                  BIGINT,
    fecha_registro             TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    imagen_url                 VARCHAR(255),
    year                       SMALLINT NOT NULL,
    numero_identificacion_hash VARCHAR(255)
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
    id_credencial BIGINT
);

ALTER TABLE usuarios
    ADD CONSTRAINT uc_usuarios_id_credencial UNIQUE (id_credencial);

ALTER TABLE usuarios
    ADD CONSTRAINT FK_USUARIOS_ON_ID_CREDENCIAL FOREIGN KEY (id_credencial) REFERENCES credenciales (id_credencial);

ALTER TABLE usuarios
    ADD CONSTRAINT FK_USUARIOS_ON_ID_ROL FOREIGN KEY (id_rol) REFERENCES roles (id_rol);