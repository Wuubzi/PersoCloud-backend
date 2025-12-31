package com.app.demo.DTO.Request;

import lombok.Data;

@Data
public class PersonaRequestDTO {
    private String primer_nombre;
    private String segundo_nombre;
    private String primer_apellido;
    private String segundo_apellido;
    private String numero_identificacion;
    private String telefono;
    private Boolean estado_votacion;
    private Long id_barrio;
}
