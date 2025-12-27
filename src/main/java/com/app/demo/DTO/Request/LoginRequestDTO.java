package com.app.demo.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email
    @NotNull
    private String correo;
    @NotNull
    private String contrasena;
}
