package com.app.demo.Controllers;

import com.app.demo.DTO.Request.DepartamentoRequestDTO;
import com.app.demo.DTO.Response.DepartamentoResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Services.DepartamentoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/v1/departamentos")
public class DepartamentosController {

    private final DepartamentoService departamentoService;

    @Autowired
    public DepartamentosController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @RequestMapping("/departamentos")
    public Page<DepartamentoResponseDTO> getDepartamentos(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search ){
        return departamentoService.getDepartamentos(page,size,search);
    }

    @RequestMapping("/departamento")
    public DepartamentoResponseDTO getDepartamento(@RequestParam Long idDepartamento){
        return departamentoService.getDepartamento(idDepartamento);
    }

    @RequestMapping("/departamentosExport")
    public List<DepartamentoResponseDTO> getDepartamentosExport(){
        return departamentoService.getDepartamentosExport();
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> crearDepartamento(@RequestBody DepartamentoRequestDTO departamento, HttpServletRequest request){
        return new ResponseEntity<>(departamentoService.crearDepartamento(departamento, request), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> actualizarDepartamento(@RequestParam Long idDepartamento, @RequestBody DepartamentoRequestDTO departamento, HttpServletRequest request){
        return new ResponseEntity<>(departamentoService.actualizarDepartamento(idDepartamento,departamento, request), HttpStatus.OK);
    }


}
