INSERT INTO roles(nombre_rol) VALUES
                                  ('COORDINADOR'),
                                  ('LÍDER'),
                                  ('SUBLÍDER');

INSERT INTO modo_sistema(modo) VALUES (false);

INSERT INTO credenciales(correo, contrasena) VALUES ('juan.perez@gmail.com','$2a$10$8mOiC1yI90ovgYV3l0YfQOSK2v9FwMSPQ/5R8fHSTBvr6RCPe/6De');



INSERT INTO usuarios(nombre,apellido,estado,id_rol,id_credencial) VALUES ('Juan','Perez',true,1,1);


