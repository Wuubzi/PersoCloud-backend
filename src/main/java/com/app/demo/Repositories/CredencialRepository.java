package com.app.demo.Repositories;

import com.app.demo.Models.Credencial;
import com.app.demo.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {
}
