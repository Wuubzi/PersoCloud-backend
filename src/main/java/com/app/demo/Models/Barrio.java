package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @OneToMany(mappedBy = "barrio")
    private List<Persona> persona;
    @ManyToOne
    @JoinColumn(name = "id_ciudad")
    private Ciudad ciudad;
}
