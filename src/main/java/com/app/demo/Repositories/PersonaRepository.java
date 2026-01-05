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
    int countAllByBarrio_IdBarrioAndYear(Long idBarrio, Short year);
    int countAllByBarrio_IdBarrioAndEstadoVotacionAndYear(Long idBarrio, Boolean estadoVotacion, Short year);
    Optional<Persona> findByNumeroIdentificacionHashAndYearAndIdPersonaNot(String numeroIdentificacionHash, Short year, Long idPersona);
    Optional<Persona> findByNumeroIdentificacionHashAndYear(String numeroIdentificacionHash, Short year);
    List<Persona> findByFechaRegistroBetweenAndBarrio_IdBarrio(
            LocalDateTime inicio,
            LocalDateTime fin,
            Long idBarrio
    );
    List<Persona> findAllByBarrio_IdBarrio(Long idBarrio);

}
