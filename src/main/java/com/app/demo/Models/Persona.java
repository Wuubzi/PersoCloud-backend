package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "personas")
@Data
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;
    @Column(name = "primer_nombre")
    private String primerNombre;
    @Column(name = "segundo_nombre")
    private String segundoNombre;
    @Column(name = "primer_apellido")
    private String primerApellido;
    @Column(name = "segundo_apellido")
    private String segundoApellido;
    @Column(name = "numero_identificacion")
    private String numeroIdentificacion;
    private String telefono;
    @Column(name = "estado_votacion")
    private Boolean estadoVotacion;
    @Column(name = "id_barrio")
    private Long idBarrio;
    @Column(name = "lugar_votacion")
    private String lugarVotacion;
    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    @Column(name = "numero_identificacion_hash")
    private String numeroIdentificacionHash;
}
