package com.app.demo.Repositories;

import com.app.demo.Models.PuestoVotacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PuestoVotacionRepository extends JpaRepository<PuestoVotacion, Long>, JpaSpecificationExecutor<PuestoVotacion> {
    List<PuestoVotacion> findAllByCiudad_IdCiudad(Long idCiudad);
}
