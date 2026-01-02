package com.app.demo.Controllers;

import com.app.demo.DTO.Request.PersonaRequestDTO;
import com.app.demo.DTO.Response.*;
import com.app.demo.Services.PersonaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {

    private final PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }


    @GetMapping("/stats")
    public ResponseEntity<PersonaStatsResponseDTO> getStats(@RequestParam Long idBarrio){
     return new ResponseEntity<>(personaService.getStats(idBarrio), HttpStatus.OK);
    }
    @GetMapping("/personas")
    public ResponseEntity<Page<PersonaResponseDTO>> getPersonas(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long barrio,
            @RequestParam(required = false) Boolean estado_votacion
    ){
        return new ResponseEntity<>(personaService.getPersonas(page,size,search,estado_votacion, barrio), HttpStatus.OK);
    }

    @GetMapping("/personasBarrio")
    public ResponseEntity<Page<PersonaResponseDTO>> getPersonasBarrio(
            @RequestParam Long idBarrio,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean estado_votacion
    )
    {
        return new ResponseEntity<>(personaService.getPersonasBarrio(idBarrio,page,size,search,estado_votacion), HttpStatus.OK);
    }

    @GetMapping("/personasExport")
    public ResponseEntity<List<PersonaResponseDTO>> getPersonasExport(){
        return new ResponseEntity<>(personaService.getPersonasExport(), HttpStatus.OK);
    }

    @GetMapping("/persona")
    public ResponseEntity<PersonaResponseDTO> getPersona(@RequestParam Long idPersona){
        return new ResponseEntity<>(personaService.getPersona(idPersona), HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> crear(@RequestParam String correo, @Valid @RequestBody PersonaRequestDTO persona, HttpServletRequest request) throws Exception {
      return new ResponseEntity<>(personaService.crear(correo, persona, request), HttpStatus.OK);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> actualizar(@RequestParam String correo, @RequestParam Long idPersona, @Valid @RequestBody PersonaRequestDTO persona, HttpServletRequest request) throws Exception {
      return new ResponseEntity<>(personaService.actualizar(correo, idPersona, persona, request), HttpStatus.OK);
    }
}
