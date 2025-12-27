package com.app.demo.Controllers;

import com.app.demo.DTO.Response.CoordinadorResponseDTO;
import com.app.demo.DTO.Response.StatsDashboardResponseDTO;
import com.app.demo.DTO.Response.SummaryResponseDTO;
import com.app.demo.Services.CoordinadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coordinador")
public class CoordinadorController {

    private final CoordinadorService coordinadorService;

    @Autowired
    public CoordinadorController(CoordinadorService coordinadorService) {
        this.coordinadorService = coordinadorService;
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @RequestMapping("/getCoordinador")
    public ResponseEntity<CoordinadorResponseDTO> getCoordinador(@RequestParam String correo) {
        return new ResponseEntity<>(coordinadorService.getCoordinador(correo), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @RequestMapping("/stats")
    public ResponseEntity<StatsDashboardResponseDTO> getStatsDashboard() {
       return new ResponseEntity<>(coordinadorService.getStatsDashboard(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @RequestMapping("/actividadReciente")
    public String getActividadReciente() {
        return "Actividad reciente";
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @RequestMapping("/summary")
    public ResponseEntity<SummaryResponseDTO> summary() {
       return new ResponseEntity<>(coordinadorService.summary(), HttpStatus.OK);
    }




}
