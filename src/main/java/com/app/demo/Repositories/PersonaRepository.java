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
    int countByUsuarioIdUsuarioAndYear(Long idLider, Short year);
    int countByUsuarioIdUsuarioAndEstadoVotacionAndYear(Long idLider, Boolean estadoVotacion, Short year);
    Optional<Persona> findByNumeroIdentificacionHashAndYearAndIdPersonaNot(String numeroIdentificacionHash, Short year, Long idPersona);
    Optional<Persona> findByNumeroIdentificacionHashAndYear(String numeroIdentificacionHash, Short year);
    int countByUsuarioIdUsuarioAndFechaRegistroBetween(
            Long idLider,
            LocalDateTime inicio,
            LocalDateTime fin
    );


}
