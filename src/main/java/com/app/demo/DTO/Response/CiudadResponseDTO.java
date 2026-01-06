package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class CiudadResponseDTO {
    private Long id_ciudad;
    private String nombre;
    private String departamento;
    private Long id_departamento;
}
