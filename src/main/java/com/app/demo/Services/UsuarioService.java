package com.app.demo.Services;

import com.app.demo.DTO.Response.UsuariosResponseDTO;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuariosResponseDTO> getUsuarios(){
        return usuarioRepository.findAllByRol_NombreRolOrRol_NombreRol("LÍDER", "SUBLÍDER").stream().map(this::mapToDTO).toList();
    }

    private UsuariosResponseDTO mapToDTO(Usuario usuario){
        UsuariosResponseDTO usuariosResponseDTO = new UsuariosResponseDTO();
        usuariosResponseDTO.setId_usuario(usuario.getIdUsuario());
        usuariosResponseDTO.setNombre(usuario.getNombre() + " " + usuario.getApellido());
        return usuariosResponseDTO;
    }
}
