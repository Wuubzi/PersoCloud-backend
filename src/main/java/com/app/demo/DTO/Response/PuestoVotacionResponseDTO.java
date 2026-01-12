package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class PuestoVotacionResponseDTO {
    private Long id_puesto_votacion;
    private String nombre;
    private Long id_ciudad;
    private String ciudad;
}
