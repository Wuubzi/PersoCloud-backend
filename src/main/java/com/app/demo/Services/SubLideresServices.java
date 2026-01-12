package com.app.demo.Services;

import com.app.demo.DTO.Request.*;
import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.DTO.Response.SubLiderResponseDTO;
import com.app.demo.Models.*;
import com.app.demo.Repositories.*;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class SubLideresServices {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CiudadRepository ciudadRepository;
    private final CredencialRepository credencialRepository;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;
    private final DateFormat dateFormat;

    @Autowired
    public SubLideresServices(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            CiudadRepository ciudadRepository,
            CredencialRepository credencialRepository,
            AuditoriaService auditoriaService,
            PasswordEncoder passwordEncoder,
            DateFormat dateFormat
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.ciudadRepository = ciudadRepository;
        this.credencialRepository = credencialRepository;
        this.auditoriaService = auditoriaService;
        this.passwordEncoder = passwordEncoder;
        this.dateFormat = dateFormat;
    }

    // 🔹 Obtener sublíderes de un líder
    public Page<SubLiderResponseDTO> getSubLideres(String correoLider, int page, int size, String search, Boolean estado) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("rol").get("nombreRol"), "SUBLÍDER")
        );


        spec = spec.and((root, query, cb) ->
                cb.equal(
                        root.get("lider")
                                .get("credencial")
                                .get("correo"),
                        correoLider
                )
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


    public List<SubLiderResponseDTO> getSubLideresExport(String correoLider) {
        return usuarioRepository.findAllByRol_NombreRolAndLider_Credencial_Correo("SUBLÍDER",correoLider).stream().map(this::mapToDTO).toList();
    }
    // 🔹 Obtener un sublíder específico
    public LiderResponseDTO getSubLider(Long idSubLider) {
        Usuario sublider = usuarioRepository.findById(idSubLider)
                .orElseThrow(() -> new EntityNotFoundException("El sublíder no existe"));

        // Verificar que sea un sublíder (tiene líder asignado)
        if (sublider.getLider() == null) {
            throw new RuntimeException("El usuario no es un sublíder");
        }

        return null;
    }

    // 🔹 Crear sublíder
    public ResponseDTO createSubLider(
            String correoLider,
            SubLiderRequestDTO data,
            HttpServletRequest request
    ) {

        Optional<Usuario> liderOptional = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);

        if (liderOptional.isEmpty()) {
            throw new RuntimeException("El líder no existe");
        }
        if (usuarioRepository.findUsuarioByCredencial_Correo(data.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya existe");
        }

        Rol rol = rolRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("El rol no existe"));
        Credencial credencial = new Credencial();
        credencial.setCorreo(data.getCorreo());
        credencial.setContrasena(passwordEncoder.encode(data.getContrasena()));
        Credencial savedCredencial = credencialRepository.save(credencial);

        Usuario sublider = new Usuario();
        sublider.setNombre(data.getNombre());
        sublider.setApellido(data.getApellido());
        sublider.setEstado(true);
        sublider.setRol(rol);
        sublider.setCredencial(savedCredencial);
        sublider.setLider(liderOptional.get()); // ⭐ RELACIÓN CLAVE
        sublider.setCiudad(liderOptional.get().getCiudad());

        usuarioRepository.save(sublider);
        auditoriaService.saveAuditoria(
                correoLider,
                "Nuevo SubLíder creado para líder " + liderOptional.get().getNombre()
        );

        return getresponseDTO("SubLíder creado correctamente", 201, request);
    }

    // 🔹 Actualizar sublíder
    public ResponseDTO updateSubLider(
            String correoUsuario,
            Long idSubLider,
            SubLiderUpdateRequestDTO data,
            HttpServletRequest request
    ) {
        Usuario sublider = usuarioRepository.findById(idSubLider)
                .orElseThrow(() -> new EntityNotFoundException("El sublíder no existe"));

        // Verificar que sea un sublíder
        if (sublider.getLider() == null) {
            throw new RuntimeException("El usuario no es un sublíder");
        }

        // Validar que el correo no exista en otro usuario
        if (data.getCorreo() != null && !data.getCorreo().isBlank()) {
            Optional<Usuario> usuarioConCorreo = usuarioRepository
                    .findUsuarioByCredencial_Correo(data.getCorreo());

            if (usuarioConCorreo.isPresent() &&
                    !usuarioConCorreo.get().getIdUsuario().equals(idSubLider)) {
                throw new RuntimeException("El correo ya existe");
            }
        }

        sublider.setNombre(data.getNombre());
        sublider.setApellido(data.getApellido());
        if (data.getCorreo() != null && !data.getCorreo().isBlank()) {
            sublider.getCredencial().setCorreo(data.getCorreo());
        }

        Usuario saved = usuarioRepository.save(sublider);

        auditoriaService.saveAuditoria(
                correoUsuario,
                "SubLíder " + saved.getNombre() + " actualizado"
        );

        return getresponseDTO("SubLíder actualizado exitosamente", 200, request);
    }

    // 🔹 Cambiar contraseña del sublíder
    public ResponseDTO changePassword(
            String correoUsuario,
            Long idSubLider,
            LiderChangePassword data,
            HttpServletRequest request
    ) {
        Usuario sublider = usuarioRepository.findById(idSubLider)
                .orElseThrow(() -> new EntityNotFoundException("El sublíder no existe"));

        // Verificar que sea un sublíder
        if (sublider.getLider() == null) {
            throw new RuntimeException("El usuario no es un sublíder");
        }

        sublider.getCredencial().setContrasena(passwordEncoder.encode(data.getContrasena()));
        usuarioRepository.save(sublider);

        auditoriaService.saveAuditoria(
                correoUsuario,
                "Contraseña del SubLíder " + sublider.getNombre() + " actualizada"
        );

        return getresponseDTO("Contraseña cambiada exitosamente", 200, request);
    }

    // 🔹 Cambiar estado del sublíder
    public ResponseDTO changeStatus(
            String correoUsuario,
            Long idSubLider,
            HttpServletRequest request
    ) {
        Usuario sublider = usuarioRepository.findById(idSubLider)
                .orElseThrow(() -> new EntityNotFoundException("El sublíder no existe"));

        // Verificar que sea un sublíder
        if (sublider.getLider() == null) {
            throw new RuntimeException("El usuario no es un sublíder");
        }

        sublider.setEstado(!sublider.getEstado());
        usuarioRepository.save(sublider);

        auditoriaService.saveAuditoria(
                correoUsuario,
                "Estado del SubLíder " + sublider.getNombre() + " actualizado"
        );

        return getresponseDTO("Estado actualizado exitosamente", 200, request);
    }



    // ===================== MAPPER =====================

    private SubLiderResponseDTO mapToDTO(Usuario usuario) {
        SubLiderResponseDTO dto = new SubLiderResponseDTO();
        dto.setId_sublider(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCredencial().getCorreo());
        dto.setEstado(usuario.getEstado());
        dto.setId_lider(usuario.getLider().getIdUsuario());
        dto.setNombre_lider(usuario.getLider().getNombre());
        return dto;
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