package com.app.demo.Repositories;

import com.app.demo.Models.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long>, JpaSpecificationExecutor<Persona> {
    int countAllByEstadoVotacion(Boolean estadoVotacion);
    int countAllByBarrio_IdBarrio(Long idBarrio);
    int countAllByBarrio_IdBarrioAndEstadoVotacion(Long idBarrio, Boolean estadoVotacion);
    Optional<Persona> findByNumeroIdentificacionHash(String numeroIdentificacionHash);
    Optional<Persona> findByNumeroIdentificacionHashAndIdPersonaNot(String numeroIdentificacionHash, Long idPersona);
    List<Persona> findByFechaRegistroBetween(
            LocalDateTime inicio,
            LocalDateTime fin
    );

}
