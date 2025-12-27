package com.app.demo.Controllers;

import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.PersonaResponseDTO;
import com.app.demo.Services.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {

    private final PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @GetMapping("/personas")
    public ResponseEntity<Page<PersonaResponseDTO>> getLideres(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String numero_identificacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) Boolean estado_votacion
    ){
        return new ResponseEntity<>(personaService.getPersonas(page,size,nombre,numero_identificacion,telefono,estado_votacion), HttpStatus.OK);
    }
}
