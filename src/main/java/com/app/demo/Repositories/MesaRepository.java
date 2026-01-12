package com.app.demo.Repositories;

import com.app.demo.Models.Mesa;
import com.app.demo.Models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long>, JpaSpecificationExecutor<Mesa> {
    List<Mesa> findAllByPuestoVotacion_IdPuestoVotacion(Long idPuestoVotacion);
}
