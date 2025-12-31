package com.app.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "modo_sistema")
@Data
public class ModoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modo_sistema")
    private Long idModoSistema;
    private Boolean modo;

}
