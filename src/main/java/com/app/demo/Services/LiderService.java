package com.app.demo.Services;

import com.app.demo.DTO.Request.LiderRequestDTO;
import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Credencial;
import com.app.demo.Models.Rol;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.CredencialRepository;
import com.app.demo.Repositories.RolRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class LiderService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final DateFormat dateFormat;
    private final PasswordEncoder passwordEncoder;
    private final CredencialRepository credencialRepository;

    @Autowired
    public LiderService(UsuarioRepository usuarioRepository,
                        DateFormat dateFormat,
                        PasswordEncoder passwordEncoder,
                        RolRepository rolRepository, CredencialRepository credencialRepository) {
        this.usuarioRepository = usuarioRepository;
        this.dateFormat = dateFormat;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.credencialRepository = credencialRepository;
    }


    public Page<LiderResponseDTO> getLideres(int page, int size, String nombre, String apellido, String correo, Boolean estado){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("rol").get("nombreRol"), "LÍDER")
        );

        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%")
            );
        }

        if (apellido != null && !apellido.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("apellido")), "%" + apellido.toLowerCase() + "%")
            );
        }

        if(correo != null && !correo.isBlank()){
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("credencial").get("correo"), correo)
            );
        }
        if(estado != null){
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("estado"), estado)
            );
        }

        Page<Usuario> usuarios = usuarioRepository.findAll(spec,pageable);

        return usuarios.map(this::mapToDTO);
    }

    public LiderResponseDTO getLider(Long idUsuario){
        Optional<Usuario> optionalUsuario = usuarioRepository.findByIdUsuarioAndRol_NombreRol(idUsuario, "LÍDER");
        if (optionalUsuario.isEmpty()) {
            throw new EntityNotFoundException("El usuario no existe");
        }

        LiderResponseDTO lider = new LiderResponseDTO();
        lider.setId_lider(optionalUsuario.get().getIdUsuario());
        lider.setNombre(optionalUsuario.get().getNombre());
        lider.setApellido(optionalUsuario.get().getApellido());
        lider.setCorreo(optionalUsuario.get().getCredencial().getCorreo());
        lider.setEstado(optionalUsuario.get().getEstado());
        return lider;
    }

    public ResponseDTO createLider(LiderRequestDTO lider, HttpServletRequest request){
        Optional<Usuario> usuarioOptional = usuarioRepository.findUsuarioByCredencial_Correo(lider.getCorreo());
        Optional<Rol> rolOptional = rolRepository.findById(2L);
        if (rolOptional.isEmpty()) {
            throw new RuntimeException("El rol no existe");
        }
        if(usuarioOptional.isPresent()){
            throw new RuntimeException("El correo ya existe");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(lider.getNombre());
        usuario.setApellido(lider.getApellido());
        usuario.setEstado(true);
        Credencial credencial = new Credencial();
        credencial.setCorreo(lider.getCorreo());
        credencial.setContrasena(passwordEncoder.encode(lider.getContrasena()));
       Credencial savedCredencial = credencialRepository.save(credencial);
        usuario.setCredencial(savedCredencial);
        usuario.setRol(rolOptional.get());
        usuarioRepository.save(usuario);

     return getresponseDTO("Líder Creado Exitosamente", 200, request);
    }
    public ResponseDTO updateLider(Long idUsuario, LiderRequestDTO lider, HttpServletRequest request){
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(idUsuario, "LÍDER");
        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("El usuario no existe");
        }

        Usuario usuario = usuarioOptional.get();
        if (lider.getCorreo() != null && !lider.getCorreo().isBlank()) {
            usuario.getCredencial().setCorreo(lider.getCorreo());
        }

        usuario.setNombre(lider.getNombre());
        usuario.setApellido(lider.getApellido());
        usuario.getCredencial().setCorreo(lider.getCorreo());
        usuario.getCredencial().setContrasena(passwordEncoder.encode(lider.getContrasena()));
        usuarioRepository.save(usuario);
        return getresponseDTO("Líder Actualizado Exitosamente", 201, request);
    }
    public ResponseDTO inactiveLider(Long idUsuario, HttpServletRequest request){
       Optional<Usuario> usuarioOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(idUsuario, "LÍDER");
        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("El usuario no existe");
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
        return getresponseDTO("Líder Inactivado Exitosamente", 200, request);
    }

    private LiderResponseDTO mapToDTO(Usuario usuario) {
        LiderResponseDTO lider = new LiderResponseDTO();
        lider.setId_lider(usuario.getIdUsuario());
        lider.setNombre(usuario.getNombre());
        lider.setApellido(usuario.getApellido());
        lider.setCorreo(usuario.getCredencial().getCorreo());
        lider.setEstado(usuario.getEstado());
        return lider;
    }
    private ResponseDTO getresponseDTO(String message, int status, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(dateFormat.getDate());
        responseDTO.setMessage(message);
        responseDTO.setStatus(status);
        responseDTO.setUrl(request.getRequestURI().replace(request.getContextPath(), ""));
        return responseDTO;
    }
}
