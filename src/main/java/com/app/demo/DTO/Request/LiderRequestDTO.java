package com.app.demo.DTO.Request;

import lombok.Data;

@Data
public class LiderRequestDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
}
