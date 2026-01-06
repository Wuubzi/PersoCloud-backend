INSERT INTO roles(nombre_rol) VALUES
                                  ('COORDINADOR'),
                                  ('LÍDER');

INSERT INTO modo_sistema(modo) VALUES (false);

INSERT INTO credenciales(correo, contrasena) VALUES
                                                 ('juan.perez@gmail.com','$2a$10$8mOiC1yI90ovgYV3l0YfQOSK2v9FwMSPQ/5R8fHSTBvr6RCPe/6De'),
                                                 ('fernando.alvarez@gmail.com', '$2a$10$JO2654v9hDuiFTcWl6jqcuFnFWEc4dP6h2.V6HwEVSKKAoRnj9vJK'),
                                                 ('ana.gomez@gmail.com', ''),
                                                 ('carlos.rodriguez@gmail.com', ''),
                                                 ('maria.lopez@gmail.com', ''),
                                                 ('luis.martinez@gmail.com', ''),
                                                 ('sofia.hernandez@gmail.com', ''),
                                                 ('pedro.ramirez@gmail.com', ''),
                                                 ('laura.torres@gmail.com', ''),
                                                 ('andres.vargas@gmail.com', ''),
                                                 ('camila.rojas@gmail.com', ''),
                                                 ('diego.castro@gmail.com', ''),
                                                 ('valentina.moreno@gmail.com', ''),
                                                 ('jorge.suarez@gmail.com', ''),
                                                 ('paula.diaz@gmail.com', '');



INSERT INTO usuarios(nombre,apellido,estado,id_rol,id_credencial) VALUES
                                                                      ('Juan','Perez',true,1,1),
                                                                      ('Fernando','Alvarez',true,2,2),
                                                                      ('Ana','Gomez',true,2,3),
                                                                      ('Carlos','Rodriguez',true,2,4),
                                                                      ('Maria','Lopez',true,2,5),
                                                                      ('Luis','Martinez',true,2,6),
                                                                      ('Sofia','Hernandez',true,2,7),
                                                                      ('Pedro','Ramirez',true,2,8),
                                                                      ('Laura','Torres',true,2,9),
                                                                      ('Andres','Vargas',true,2,10),
                                                                      ('Camila','Rojas',true,2,11),
                                                                      ('Diego','Castro',true,2,12),
                                                                      ('Valentina','Moreno',true,2,13),
                                                                      ('Jorge','Suarez',true,2,14),
                                                                      ('Paula','Diaz',true,2,15);



INSERT INTO departamentos(nombre_departamento) VALUES ('Atlantico');

INSERT INTO ciudades(nombre_ciudad, id_departamento) VALUES ('Soledad',1);
INSERT INTO barrios(nombre_barrio, estado, id_usuario, id_ciudad) VALUES
                                                         ('Villa Cristina',true,2, 1),
                                                         ('Villa San Francisco',true,3, 1),
                                                         ('Villa San Juan',true,4,1 ),
                                                         ('El poblado', true, 5, 1),
                                                         ('Villa Katanga', true, 6, 1),
                                                         ('El Hipódromo', true, 7,1 ),
                                                         ('Los robles', true, 8, 1),
                                                         ('Nueva Esperanza', true, 9, 1),
                                                         ('El Carmen', true, 10, 1),
                                                         ('Soledad 2000', true, 11, 1),
                                                         ('Las Margaritas', true, 12, 1),
                                                         ('El Carmen', true, 13, 1),
                                                         ('Las Colonias', true , 14, 1),
                                                         ('Ciudad Camelot', true, 15, 1);
