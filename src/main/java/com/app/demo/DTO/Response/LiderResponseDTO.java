package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class LiderResponseDTO {
    private Long id_lider;
    private String nombre;
    private String apellido;
    private String correo;
    private Boolean estado;
    private Long id_ciudad;
    private Long id_departamento;
    private String departamento;
    private String ciudad;
}
