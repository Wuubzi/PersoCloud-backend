package com.app.demo.Models;


import jakarta.persistence.*;
import lombok.Data;

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
    @OneToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
    @OneToOne
    @JoinColumn(name = "id_credencial")
    private Credencial credencial;

}
