package com.app.demo.Services;

import com.app.demo.DTO.Response.UsuariosResponseDTO;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuariosResponseDTO> getUsuarios(Long idLider){
        List<Usuario> resultado = new ArrayList<>();

        Optional<Usuario> lider = usuarioRepository.findById(idLider);
        if (lider.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }
        resultado.add(lider.get());



        List<Usuario> sublideres = usuarioRepository.findAllByLider_IdUsuario(idLider);
        resultado.addAll(sublideres);

        return resultado.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private UsuariosResponseDTO mapToDTO(Usuario usuario){
        UsuariosResponseDTO usuariosResponseDTO = new UsuariosResponseDTO();
        usuariosResponseDTO.setId_usuario(usuario.getIdUsuario());
        usuariosResponseDTO.setNombre(usuario.getNombre() + " " + usuario.getApellido());
        return usuariosResponseDTO;
    }
}
