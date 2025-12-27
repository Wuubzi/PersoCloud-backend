package com.app.demo.Controllers;

import com.app.demo.DTO.Request.BarrioRequestDTO;
import com.app.demo.DTO.Response.BarrioResponseDTO;
import com.app.demo.Services.BarrioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/barrios")
public class BarrioController {

    private final BarrioService barrioService;

    @Autowired
    public BarrioController(BarrioService barrioService) {
        this.barrioService = barrioService;
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @GetMapping("/barrios")
    public ResponseEntity<Page<BarrioResponseDTO>> getBarrios(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String lider_nombre){
        return new ResponseEntity<>(barrioService.getBarrios(page,size,nombre,lider_nombre), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @GetMapping("/barrio")
    public ResponseEntity<BarrioResponseDTO> getBarrio(@RequestParam Long idBarrio){
        return new ResponseEntity<>(barrioService.getBarrio(idBarrio), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @PostMapping("/crear")
    public ResponseEntity<Object> crearBarrio(@Valid @RequestBody BarrioRequestDTO barrio, HttpServletRequest request){
        return new ResponseEntity<>(barrioService.crearBarrio(barrio, request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @PutMapping("/actualizar")
    public ResponseEntity<Object> actualizarBarrio(@RequestParam Long idUsuario, @Valid @RequestBody BarrioRequestDTO barrio, HttpServletRequest request){
        return new ResponseEntity<>(barrioService.actualizarBarrio(idUsuario,barrio, request), HttpStatus.CREATED);
    }











}
