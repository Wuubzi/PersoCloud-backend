package com.app.demo.Controllers;

import com.app.demo.DTO.Request.MesaRequestDTO;
import com.app.demo.DTO.Response.MesaResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Services.MesaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesas")
public class MesaController {

    private final MesaService mesaservice;

    @Autowired
    public MesaController(MesaService mesaservice) {
        this.mesaservice = mesaservice;
    }

    @GetMapping("/mesas")
    public ResponseEntity<Page<MesaResponseDTO>> getMesas(
            @RequestParam String correoLider,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long idPuestoVotacion
    ) {
        return new ResponseEntity<>(mesaservice.Mesas(correoLider, page, size, search, idPuestoVotacion), HttpStatus.OK);
    }

    @GetMapping("/mesasByPuesto")
    public ResponseEntity<List<MesaResponseDTO>> getMesasByPuesto(@RequestParam Long idPuestoVotacion){
        return new ResponseEntity<>(mesaservice.getMesasByPuesto(idPuestoVotacion), HttpStatus.OK);
    }

    @GetMapping("/mesasExport")
    public ResponseEntity<List<MesaResponseDTO>> getMesasExport(@RequestParam String correoLider){
        return new ResponseEntity<>(mesaservice.getMesasExport(correoLider), HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> crear(@RequestParam String correoLider, @RequestBody MesaRequestDTO mesas, HttpServletRequest request) {
        return new ResponseEntity<>(mesaservice.crearMesa(correoLider,mesas,request), HttpStatus.CREATED);
    }

        @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> actualizar(@RequestParam String correoLider,@RequestParam Long idMesa, @RequestBody MesaRequestDTO mesas, HttpServletRequest request) {
        return new ResponseEntity<>(mesaservice.updateMesa(correoLider,idMesa,mesas,request), HttpStatus.OK);

    }
}
