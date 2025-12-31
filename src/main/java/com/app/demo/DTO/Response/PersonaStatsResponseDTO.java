package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class PersonaStatsResponseDTO {
    private int totalPersonas;
    private int totalVotaron;
    private int totalSinVotar;
    private int registradosHoy;
    private double progresoVotacion;
}
