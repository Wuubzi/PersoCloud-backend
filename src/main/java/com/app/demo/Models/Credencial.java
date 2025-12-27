package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "credenciales")
@Data
public class Credencial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credencial")
    private Long idCredencial;
    private String correo;
    private String contrasena;
    @OneToOne(mappedBy = "credencial", cascade = CascadeType.ALL)
    private Usuario usuario;
}
