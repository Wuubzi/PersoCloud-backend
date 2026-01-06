package com.app.demo.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartamentoRequestDTO {
    @NotNull
    private String nombre_departamento;
}
