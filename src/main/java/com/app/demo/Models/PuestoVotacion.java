package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "puesto_votacion")
@Data
public class PuestoVotacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puesto_votacion")
    private Long idPuestoVotacion;

    @Column(name = "nombre_puesto")
    private String nombrePuesto;

    // MUCHOS puestos pertenecen a UNA ciudad
    @ManyToOne
    @JoinColumn(name = "id_ciudad", nullable = false)
    private Ciudad ciudad;

    // UN puesto tiene MUCHAS mesas
    @OneToMany(mappedBy = "puestoVotacion")
    private List<Mesa> mesas;
}

