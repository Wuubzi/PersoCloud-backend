package com.app.demo.Repositories;

import com.app.demo.Models.Barrio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarrioRepository extends JpaRepository<Barrio, Long>, JpaSpecificationExecutor<Barrio> {

    Optional<Barrio> findBarrioByNombreBarrio(String nombreBarrio);
    Optional<Barrio> findBarrioByIdLider(Long idLider);
}
