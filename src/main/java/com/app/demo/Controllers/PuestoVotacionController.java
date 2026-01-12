package com.app.demo.Controllers;

import com.app.demo.DTO.Request.PuestoVotacionRequestDTO;
import com.app.demo.DTO.Response.PuestoVotacionResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Services.PuestoVotacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/v1/puestos-votacion")
public class PuestoVotacionController {
    private final PuestoVotacionService puestoVotacionService;

    @Autowired
    public PuestoVotacionController(PuestoVotacionService puestoVotacionService) {
        this.puestoVotacionService = puestoVotacionService;
    }

    @RequestMapping("/puestos-votacion")
    public ResponseEntity<Page<PuestoVotacionResponseDTO>> getPuestosVotacion(
            @RequestParam String correoLider,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search
    ){
        return ResponseEntity.ok(puestoVotacionService.PuestosVotacion(correoLider,page,size,search));
    }

    @GetMapping("/puestosByCiudad")
    public ResponseEntity<List<PuestoVotacionResponseDTO>> getPuestosByCiudad(@RequestParam Long idCiudad){
        return new ResponseEntity<>(puestoVotacionService.getPuestosVotacionByCiudad(idCiudad), HttpStatus.OK);
    }
    @GetMapping("/puestos-votacionExport")
    public ResponseEntity<List<PuestoVotacionResponseDTO>> getPuestosVotacionExport(@RequestParam String correoLider){
        return new ResponseEntity<>(puestoVotacionService.getPuestosVotacionExport(correoLider), HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> createPuestoVotacion(@RequestParam  String correoLider, @Valid @RequestBody PuestoVotacionRequestDTO puestoVotacion, HttpServletRequest request) {
        return new ResponseEntity<>(puestoVotacionService.create(correoLider,puestoVotacion,request), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> updatePuestoVotacion(@RequestParam  String correoLider, @RequestParam Long id_puestoVotacion, @Valid @RequestBody PuestoVotacionRequestDTO puestoVotacion, HttpServletRequest request) {
        return new ResponseEntity<>(puestoVotacionService.update(correoLider,id_puestoVotacion,puestoVotacion,request), HttpStatus.OK);
    }
}
