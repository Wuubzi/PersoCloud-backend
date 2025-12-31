package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class SummaryResponseDTO {
    private int totalPersonasSinVotar;
    private int totalPersonasVotaron;
    private int totalLideresActivos;
}
