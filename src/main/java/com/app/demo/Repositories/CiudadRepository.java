package com.app.demo.Repositories;

import com.app.demo.DTO.Response.CiudadResponseDTO;
import com.app.demo.Models.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long>, JpaSpecificationExecutor<Ciudad> {
    List<Ciudad> findAllByDepartamento_IdDepartamento(Long idDepartamento);
    Optional<Ciudad> findBy(Long idCiudad, Long idDepartamento);
}
