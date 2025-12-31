package com.app.demo.Controllers;

import com.app.demo.Models.Auditoria;
import com.app.demo.Services.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @Autowired
    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoriasLimit")
    public List<Auditoria> getAuditoriasLimit(){
        return auditoriaService.getAuditoriaLimit();
    }

    @GetMapping("/auditorias")
    public List<Auditoria> getAuditorias(){
        return auditoriaService.getAuditoria();
    }
}
