package com.app.demo.Repositories;

import com.app.demo.Models.ModoSistema;
import com.app.demo.Models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModoRepository extends JpaRepository<ModoSistema, Long> {
}
