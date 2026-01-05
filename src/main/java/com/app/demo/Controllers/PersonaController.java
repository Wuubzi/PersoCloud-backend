package com.app.demo.Controllers;

import com.app.demo.DTO.Request.PersonaRequestDTO;
import com.app.demo.DTO.Response.*;
import com.app.demo.Services.PersonaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(required = false) Short year,
            @RequestParam(required = false) Boolean estado_votacion
    ){
        return new ResponseEntity<>(personaService.getPersonas(page,size,search,year,estado_votacion, barrio), HttpStatus.OK);
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


    @GetMapping("/personaBarrioExport")
    public ResponseEntity<List<PersonaResponseDTO>> getPersonasBarrioExport(@RequestParam Long idBarrio){
        return new ResponseEntity<>(personaService.getPersonasBarrioExport(idBarrio), HttpStatus.OK);

    }


    @GetMapping("/persona")
    public ResponseEntity<PersonaResponseDTO> getPersona(@RequestParam Long idPersona){
        return new ResponseEntity<>(personaService.getPersona(idPersona), HttpStatus.OK);
    }

    @PostMapping(
            value = "/crear",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResponseDTO> crear(
            @RequestParam String correo,
            @RequestPart("persona") String personaJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) throws Exception {
        PersonaRequestDTO persona =
                new ObjectMapper().readValue(personaJson, PersonaRequestDTO.class);
        return new ResponseEntity<>(
                personaService.crear(correo, persona, image, request),
                HttpStatus.OK
        );
    }




    @PutMapping(
            value = "/actualizar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResponseDTO> actualizar(
            @RequestParam String correo,
            @RequestParam Long idPersona,
            @RequestPart("persona") String personaJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) throws Exception {

        PersonaRequestDTO persona =
                new ObjectMapper().readValue(personaJson, PersonaRequestDTO.class);

        return new ResponseEntity<>(
                personaService.actualizar(correo, idPersona, persona, image, request),
                HttpStatus.OK
        );
    }


}
