package com.app.demo.Repositories;

import com.app.demo.Models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface PersonaRepository extends JpaRepository<Persona, Long>, JpaSpecificationExecutor<Persona> {
    int countAllByEstadoVotacion(Boolean estadoVotacion);
}
