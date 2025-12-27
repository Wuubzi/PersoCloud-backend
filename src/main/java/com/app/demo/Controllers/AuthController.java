package com.app.demo.Controllers;

import com.app.demo.DTO.Request.LoginRequestDTO;
import com.app.demo.DTO.Response.AuthResponseDTO;
import com.app.demo.Services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

private final AuthService authService;

@Autowired
public AuthController(AuthService authService) {
    this.authService = authService;
}

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO data, HttpServletRequest request) {
       return new ResponseEntity<>(authService.login(data, request), HttpStatus.CREATED);
    }
}
