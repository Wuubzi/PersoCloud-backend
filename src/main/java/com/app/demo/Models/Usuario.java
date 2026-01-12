package com.app.demo.Models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private Boolean estado;
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
    @OneToOne
    @JoinColumn(name = "id_credencial")
    private Credencial credencial;

    @ManyToOne
    @JoinColumn(name = "id_lider")
    private Usuario lider;

    @OneToMany(mappedBy = "lider")
    private List<Usuario> sublideres;


    @OneToMany(mappedBy = "usuario")
    private java.util.List<Persona> personas;

    @ManyToOne
    @JoinColumn(name = "id_ciudad")
    private Ciudad ciudad;

}
