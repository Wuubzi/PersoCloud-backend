package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Entity
@Table(name = "departamentos")
@Data
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Long idDepartamento;
    @Column(name = "nombre_departamento")
    private String nombreDepartamento;
    @OneToMany(mappedBy = "departamento")
    private List<Ciudad> ciudad;

}
