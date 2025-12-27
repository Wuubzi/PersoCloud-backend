package com.app.demo.Controllers;

import com.app.demo.DTO.Request.LiderRequestDTO;
import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.DTO.Response.SummaryResponseDTO;
import com.app.demo.Services.LiderService;
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
@RequestMapping("/api/v1/lider")
public class LiderController {

    private final LiderService liderService;

    @Autowired
    public LiderController(LiderService liderService) {
        this.liderService = liderService;
    }

    @GetMapping("/lideres")
    public ResponseEntity<Page<LiderResponseDTO>> getLideres(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) Boolean estado
    ){
        return new ResponseEntity<>(liderService.getLideres(page,size,nombre,apellido,correo,estado), HttpStatus.OK);
    }
    @GetMapping("/lider")
    public ResponseEntity<LiderResponseDTO> getLider(@RequestParam Long idUsuario){
        return new ResponseEntity<>(liderService.getLider(idUsuario), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> createLider(@Valid @RequestBody LiderRequestDTO lider, HttpServletRequest request){
        return new ResponseEntity<>(liderService.createLider(lider, request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> updateLider(@RequestParam Long idUsuario, @Valid @RequestBody LiderRequestDTO lider, HttpServletRequest request){
        return new ResponseEntity<>(liderService.updateLider(idUsuario, lider, request), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @PutMapping("/inactivar")
    public ResponseEntity<ResponseDTO> inactiveLider(@RequestParam Long idUsuario, HttpServletRequest request){
        return new ResponseEntity<>(liderService.inactiveLider(idUsuario,request), HttpStatus.OK);
    }

}
