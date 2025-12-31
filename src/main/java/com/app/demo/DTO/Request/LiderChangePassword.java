package com.app.demo.DTO.Request;

import lombok.Data;

@Data
public class LiderChangePassword {
    private String contrasena;
    private String confirmarContrasena;
}
