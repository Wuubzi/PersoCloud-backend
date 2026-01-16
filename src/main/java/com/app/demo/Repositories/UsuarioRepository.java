package com.app.demo.Repositories;

import com.app.demo.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findUsuarioByCredencial_Correo(String correo);
    List<Usuario> findAllByRol_NombreRol(String nombreRol);

    List<Usuario> findAllByRol_NombreRolOrRol_NombreRol(String rolNombreRol, String rolNombreRol2);
    List<Usuario> findAllByRol_NombreRolAndEstado(String rol, Boolean estado);
    Optional<Usuario> findByIdUsuarioAndRol_NombreRol(Long id, String rol);
    Optional<Usuario> findByCredencial_CorreoAndRol_NombreRol(String correo, String rol);
    List<Usuario> findAllByRol_NombreRolAndLider_Credencial_Correo(String nombreRol, String idLider);
    Optional<Usuario> findByCredencial_CorreoAndRol_NombreRolIn(
            String correo,
            List<String> roles
    );


}
