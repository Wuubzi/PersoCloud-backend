package com.app.demo.DTO.Response;

import lombok.Data;

@Data
public class ResponseDTO {
    private String timestamp;
    private int status;
    private String message;
    private String url;

}
