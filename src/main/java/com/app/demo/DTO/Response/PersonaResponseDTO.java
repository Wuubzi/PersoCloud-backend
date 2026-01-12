package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class PersonaResponseDTO {
    private Long id_persona;
    private String primer_nombre;
    private String segundo_nombre;
    private String primer_apellido;
    private String segundo_apellido;
    private String numero_identificacion;
    private String telefono;
    private Boolean estado_votacion;
    private String lugar_votacion;
    private Long id_puesto_votacion;
    private Long id_mesa;
    private String barrio_nombre;
    private Long id_lider;
    private String nombre_lider;
    private String mesa;
    private String departamento;
    private String ciudad;
    private String imagen_url;
    private Short year;
}
