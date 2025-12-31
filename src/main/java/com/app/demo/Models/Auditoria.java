package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;
    @Column(name = "correo_usuario")
    private String correoUsuario;
    private String rol;
    private String accion;
    private LocalDateTime fecha = LocalDateTime.now();
}
