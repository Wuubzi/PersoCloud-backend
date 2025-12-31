package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class PersonaResponseDTO {
    private Long id_persona;
    private String nombre;
    private String numero_identificacion;
    private String telefono;
    private Boolean estado_votacion;
    private String barrio_nombre;
}
