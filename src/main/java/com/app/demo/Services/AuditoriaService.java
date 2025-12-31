package com.app.demo.Services;


import com.app.demo.DTO.Request.AuditoriaRequestDTO;
import com.app.demo.Models.Auditoria;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.AuditoriaRepository;
import com.app.demo.Repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AuditoriaService(AuditoriaRepository auditoriaRepository, UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Auditoria> getAuditoriaLimit(){
        return auditoriaRepository.findTop5ByOrderByFechaDesc();
    }

    public List<Auditoria> getAuditoria(){
        return auditoriaRepository.findAll();
    }

    public void saveAuditoria(String correoUsuario, String accion) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findUsuarioByCredencial_Correo(correoUsuario);

        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("Este usuario no existe");
        }

        Auditoria auditoria = new Auditoria();
        auditoria.setCorreoUsuario(correoUsuario);
        auditoria.setRol(usuarioOptional.get().getRol().getNombreRol());
        auditoria.setAccion(accion);
        auditoriaRepository.save(auditoria);
    }
}
