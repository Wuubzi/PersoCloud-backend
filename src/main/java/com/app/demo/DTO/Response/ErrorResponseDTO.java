package com.app.demo.DTO.Response;
import lombok.Data;

@Data
public class ErrorResponseDTO {
    private int status;
    private String exception;
    private String message;
    private String timestamp;
    private String url;
}