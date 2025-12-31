package com.app.demo.Repositories;

import com.app.demo.Models.Credencial;
import com.app.demo.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {
}
