package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class MesaResponseDTO {
    private Long id_mesa;
    private String numero_mesa;
    private Long id_puesto_votacion;
    private String nombre_puesto;
}
