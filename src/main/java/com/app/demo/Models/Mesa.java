package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mesa")
@Data
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Long idMesa;

    @Column(name = "numero_mesa")
    private String numeroMesa;

    @ManyToOne
    @JoinColumn(name = "id_puesto_votacion", nullable = false)
    private PuestoVotacion puestoVotacion;
}
