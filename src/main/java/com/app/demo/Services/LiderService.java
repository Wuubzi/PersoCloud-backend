package com.app.demo.Services;

import com.app.demo.DTO.Request.LiderChangePassword;
import com.app.demo.DTO.Request.LiderRequestDTO;
import com.app.demo.DTO.Request.LiderUpdateRequestDTO;
import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Credencial;
import com.app.demo.Models.Rol;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.AuditoriaRepository;
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


import java.util.List;
import java.util.Optional;

@Service
public class LiderService {

    private final AuditoriaService auditoriaService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final DateFormat dateFormat;
    private final PasswordEncoder passwordEncoder;
    private final CredencialRepository credencialRepository;

    @Autowired
    public LiderService(UsuarioRepository usuarioRepository,
                        DateFormat dateFormat,
                        PasswordEncoder passwordEncoder,
                        RolRepository rolRepository,
                        CredencialRepository credencialRepository,
                        AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.dateFormat = dateFormat;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.credencialRepository = credencialRepository;
        this.auditoriaService = auditoriaService;
    }


    public Page<LiderResponseDTO> getLideres(int page, int size, String search, Boolean estado){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("rol").get("nombreRol"), "LÍDER")
        );

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("nombre")), like),
                            cb.like(cb.lower(root.get("apellido")), like),
                            cb.like(cb.lower(root.get("credencial").get("correo")), like)
                    )
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

    public List<LiderResponseDTO> getLiderExport() {
        return usuarioRepository.findAllByRol_NombreRol("LÍDER").stream().map(this::mapToDTO).toList();
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

    public LiderResponseDTO getLiderCorreo(String correo){
        Optional<Usuario> optionalUsuario = usuarioRepository.findByCredencial_CorreoAndRol_NombreRol(correo, "LÍDER");
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

    public ResponseDTO createLider(String correoUsuario, LiderRequestDTO lider, HttpServletRequest request){
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
        this.auditoriaService.saveAuditoria(correoUsuario, "Nuevo Lider Agregado");

     return getresponseDTO("Líder Creado Exitosamente", 200, request);
    }


    public ResponseDTO updateLider(String correoUsuario,Long idUsuario, LiderUpdateRequestDTO lider, HttpServletRequest request){
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
        Usuario saved = usuarioRepository.save(usuario);
        this.auditoriaService.saveAuditoria(correoUsuario, "Líder " + saved.getNombre() + " Actualizado");
        return getresponseDTO("Líder Actualizado Exitosamente", 201, request);
    }

    public ResponseDTO changePassword(String correoUsuario,Long idUsuario, LiderChangePassword data, HttpServletRequest request){
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(idUsuario, "LÍDER");
        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("El usuario no existe");
        }
        Usuario usuario = usuarioOptional.get();
        usuario.getCredencial().setContrasena(passwordEncoder.encode(data.getContrasena()));
        usuarioRepository.save(usuario);
        this.auditoriaService.saveAuditoria(correoUsuario, "Contraseña de Líder " + usuario.getNombre() + " Actualizada");
        return getresponseDTO("Contraseña cambiada Exitosamente", 200, request);
    }
    public ResponseDTO changeStatus(String correoUsuario,Long idUsuario, HttpServletRequest request){
       Optional<Usuario> usuarioOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(idUsuario, "LÍDER");
        if (usuarioOptional.isEmpty()) {
            throw new EntityNotFoundException("El usuario no existe");
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setEstado(!usuario.getEstado());
        usuarioRepository.save(usuario);
        this.auditoriaService.saveAuditoria(correoUsuario, "Estado del Líder " + usuario.getNombre() + " Actualizado");
        return getresponseDTO("Estado cambiado Exitosamente", 200, request);
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
