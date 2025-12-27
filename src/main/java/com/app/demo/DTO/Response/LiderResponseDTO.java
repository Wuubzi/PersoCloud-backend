package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class LiderResponseDTO {
    private Long id_lider;
    private String nombre;
    private String apellido;
    private String correo;
    private Boolean estado;
}
