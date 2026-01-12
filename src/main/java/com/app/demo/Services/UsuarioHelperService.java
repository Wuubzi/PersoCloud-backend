package com.app.demo.Services;

import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioHelperService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioHelperService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene el correo del líder principal.
     * Si el correo pertenece a un líder, devuelve ese mismo correo.
     * Si el correo pertenece a un sublíder, devuelve el correo de su líder asociado.
     *
     * Este método es útil para usar en cualquier consulta que necesite el correo del líder.
     */
    public String obtenerCorreoLiderPrincipal(String correo) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findUsuarioByCredencial_Correo(correo);

        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("El usuario con correo " + correo + " no existe");
        }

        Usuario usuario = usuarioOptional.get();
        String rol = usuario.getRol().getNombreRol();

        // Si es líder, devolver su propio correo
        if ("LÍDER".equalsIgnoreCase(rol)) {
            return correo;
        }

        // Si es sublíder, devolver el correo de su líder asociado
        if ("SUBLÍDER".equalsIgnoreCase(rol) || "SUBLIDER".equalsIgnoreCase(rol)) {
            if (usuario.getLider() != null && usuario.getLider().getCredencial() != null) {
                return usuario.getLider().getCredencial().getCorreo();
            } else {
                throw new EntityNotFoundException("El sublíder no tiene un líder asociado");
            }
        }

        // Para cualquier otro rol, devolver el correo original
        return correo;
    }

    /**
     * Obtiene el ID del líder principal.
     */
    public Long obtenerIdLiderPrincipal(String correo) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findUsuarioByCredencial_Correo(correo);

        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("El usuario con correo " + correo + " no existe");
        }

        Usuario usuario = usuarioOptional.get();
        String rol = usuario.getRol().getNombreRol();

        if ("LÍDER".equalsIgnoreCase(rol)) {
            return usuario.getIdUsuario();
        }

        if ("SUBLÍDER".equalsIgnoreCase(rol) || "SUBLIDER".equalsIgnoreCase(rol)) {
            if (usuario.getLider() != null) {
                return usuario.getLider().getIdUsuario();
            } else {
                throw new EntityNotFoundException("El sublíder no tiene un líder asociado");
            }
        }

        return usuario.getIdUsuario();
    }

    /**
     * Verifica si un correo pertenece a un líder
     */
    public boolean esLider(String correo) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findUsuarioByCredencial_Correo(correo);
        if (usuarioOptional.isEmpty()) {
            return false;
        }
        return "LÍDER".equalsIgnoreCase(usuarioOptional.get().getRol().getNombreRol());
    }

    /**
     * Verifica si un correo pertenece a un sublíder
     */
    public boolean esSublider(String correo) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findUsuarioByCredencial_Correo(correo);
        if (usuarioOptional.isEmpty()) {
            return false;
        }
        String rol = usuarioOptional.get().getRol().getNombreRol();
        return "SUBLÍDER".equalsIgnoreCase(rol) || "SUBLIDER".equalsIgnoreCase(rol);
    }
}