package com.app.demo.Controllers;

import com.app.demo.Services.ModoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modos")
public class ModoController {

    private final ModoService modoService;

    @Autowired
    public ModoController(ModoService modoService) {
        this.modoService = modoService;
    }

    @GetMapping("/getModo")
    public Boolean getModo(){
        return modoService.getModo();
    }

    @PutMapping("/setModo")
    public ResponseEntity<Object> setModo(String correoUsuario, HttpServletRequest request){
        return new ResponseEntity<>(modoService.setModo(correoUsuario, request), HttpStatus.CREATED);
    }

}
