package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class SubLiderResponseDTO {
    private Long id_sublider;
    private String nombre;
    private String apellido;
    private String correo;
    private Boolean estado;
    private Long id_lider;
    private String nombre_lider;
}
