package com.app.demo.Controllers;

import com.app.demo.DTO.Request.LiderChangePassword;
import com.app.demo.DTO.Request.LiderRequestDTO;
import com.app.demo.DTO.Request.SubLiderRequestDTO;
import com.app.demo.DTO.Request.SubLiderUpdateRequestDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.DTO.Response.SubLiderResponseDTO;
import com.app.demo.Services.SubLideresServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sublider")
public class SubLiderController {

    private final SubLideresServices subLideresService;

    @Autowired
    public SubLiderController(SubLideresServices subLideresService) {
        this.subLideresService = subLideresService;
    }

    // 🔹 Obtener sublíderes de un líder
    @GetMapping("/sublideres")
    public ResponseEntity<Page<SubLiderResponseDTO>> getSubLideres(
            @RequestParam String correoLider,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean estado
    ) {
        return new ResponseEntity<>(
                subLideresService.getSubLideres(correoLider,page,size,search,estado),
                HttpStatus.OK
        );
    }

    @GetMapping("/sublideresExport")
    public ResponseEntity<List<SubLiderResponseDTO>> getSubLideresExport(@RequestParam String correoLider){
        return new ResponseEntity<>(subLideresService.getSubLideresExport(correoLider),HttpStatus.OK);
    }


    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO> createSubLider(
            @RequestParam String correoLider,
            @Valid @RequestBody SubLiderRequestDTO data,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                subLideresService.createSubLider(
                        correoLider,
                        data,
                        request
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ResponseDTO> updateSubLider(
            @RequestParam String correoLider,
            @RequestParam Long idSubLider,
            @Valid @RequestBody SubLiderUpdateRequestDTO data,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(subLideresService.updateSubLider(correoLider,idSubLider,data,request),HttpStatus.OK);
    }


    @PutMapping("/cambiarContrasena")
    public ResponseEntity<ResponseDTO> changePassword(
            @RequestParam String correoUsuario,
            @RequestParam Long idSubLider,
            @Valid @RequestBody LiderChangePassword data,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(this.subLideresService.changePassword(correoUsuario, idSubLider, data, request), HttpStatus.OK);
    }

    @PutMapping("/cambiarEstado")
    public ResponseEntity<ResponseDTO> changeStatus(
            @RequestParam String correoUsuario,
            @RequestParam Long idSubLider,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                subLideresService.changeStatus(
                        correoUsuario,
                        idSubLider,
                        request
                ),
                HttpStatus.OK
        );
    }
}
