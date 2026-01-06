package com.app.demo.DTO.Request;

import lombok.Data;

@Data
public class BarrioRequestDTO {
    private String nombre_barrio;
    private Long id_lider;
    private Long id_ciudad;
}
