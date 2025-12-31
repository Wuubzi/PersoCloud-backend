package com.app.demo.Exceptions;

import com.app.demo.DTO.Response.ErrorResponseDTO;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final DateFormat dateFormat;

    @Autowired
    public GlobalExceptionHandler(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex,  WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(Exception e, WebRequest request, HttpStatus status) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setStatus(status.value());
        error.setException(e.getClass().getSimpleName());
        error.setMessage(e.getMessage());
        error.setTimestamp(dateFormat.getDate());
        error.setUrl(request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(error, status);
    }

            }
