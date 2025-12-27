package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "barrios")
@Data
public class Barrio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barrio")
    private Long idBarrio;
    @Column(name = "nombre_barrio")
    private String nombreBarrio;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "id_lider")
    private Long idLider;
}
