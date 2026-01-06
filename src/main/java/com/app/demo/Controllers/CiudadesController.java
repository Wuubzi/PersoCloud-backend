package com.app.demo.Controllers;

import com.app.demo.DTO.Request.CiudadRequestDTO;
import com.app.demo.DTO.Response.CiudadResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Services.CiudadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/v1/ciudades")
public class CiudadesController {

    private final CiudadService ciudadService;

    @Autowired
    public CiudadesController(CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @RequestMapping("/ciudades")
    public Page<CiudadResponseDTO> getCiudades(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search ){
        return ciudadService.getCiudades(page, size, search);
    }

    @RequestMapping("/ciudad")
    public CiudadResponseDTO getCiudad(Long idCiudad){
        return ciudadService.getCiudad(idCiudad);
    }

    @RequestMapping("/ciudadesExport")
    public List<CiudadResponseDTO> getCiudadesExport() {
        return ciudadService.getCiudadesExport();
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> crearCiudad(@RequestBody CiudadRequestDTO ciudad, HttpServletRequest request){
        return new ResponseEntity<>(ciudadService.crearCiudad(ciudad, request), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> actualizarCiudad(@RequestParam Long idCiudad, @RequestBody CiudadRequestDTO ciudad, HttpServletRequest request){
        return new ResponseEntity<>(ciudadService.actualizarCiudad(idCiudad,ciudad, request), HttpStatus.OK);
    }

}
