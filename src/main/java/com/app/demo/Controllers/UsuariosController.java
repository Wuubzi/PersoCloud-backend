package com.app.demo.Controllers;

import com.app.demo.DTO.Response.UsuariosResponseDTO;
import com.app.demo.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuariosResponseDTO>> getUsuarios(){
        return new ResponseEntity<>(usuarioService.getUsuarios(), HttpStatus.OK);
    }
}
