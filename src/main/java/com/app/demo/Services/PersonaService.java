package com.app.demo.Services;
import com.app.demo.DTO.Request.PersonaRequestDTO;
import com.app.demo.DTO.Response.PersonaResponseDTO;
import com.app.demo.DTO.Response.PersonaStatsResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.*;
import com.app.demo.Repositories.MesaRepository;
import com.app.demo.Repositories.PersonaRepository;
import com.app.demo.Repositories.PuestoVotacionRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.AESUtils;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MesaRepository mesaRepository;
    private final PuestoVotacionRepository puestoVotacionRepository;
    private final ImagesService imagesService;
    private final AuditoriaService auditoriaService;
    private final DateFormat dateFormat;

    @Autowired
    public PersonaService(PersonaRepository personaRepository,
                          AuditoriaService auditoriaService,
                          UsuarioRepository usuarioRepository,
                          PuestoVotacionRepository puestoVotacionRepository,
                          MesaRepository mesaRepository,
                          ImagesService imagesService,
                          DateFormat dateFormat) {
        this.personaRepository = personaRepository;
        this.dateFormat = dateFormat;
        this.imagesService = imagesService;
        this.mesaRepository = mesaRepository;
        this.puestoVotacionRepository = puestoVotacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public Page<PersonaResponseDTO> getPersonas(int page, int size, String search, Short year, Boolean estado_votacion, Long idLider, Long departamento, Long idCiudad, Long PuestoVotacion, Long mesa, Long usuarioRegistro){
        Pageable pageable = PageRequest.of(page, size);
        Specification<Persona> spec = (root, query, cb) -> cb.conjunction();

        if (year != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("year"), year)
            );
        }

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                // Nombre completo concatenado
                var nombreCompleto = cb.concat(
                        cb.concat(root.get("primerNombre"), " "),
                        cb.concat(
                                cb.concat(root.get("segundoNombre"), " "),
                                cb.concat(
                                        cb.concat(root.get("primerApellido"), " "),
                                        root.get("segundoApellido")
                                )
                        )
                );

                Join<Persona, Usuario> usuarioJoin = root.join("usuario", JoinType.LEFT);
                var nombreCompletoLider = cb.concat(
                        cb.concat(
                                cb.lower(usuarioJoin.get("nombre")),
                                " "
                        ),
                        cb.lower(usuarioJoin.get("apellido"))
                );

                String searchHash = DigestUtils.sha256Hex(search);
                return cb.or(
                        cb.like(cb.lower(nombreCompleto), like),
                        cb.equal(root.get("numeroIdentificacionHash"), searchHash),
                        cb.like(cb.lower(usuarioJoin.get("nombre")), like),
                        cb.like(nombreCompletoLider, like),
                        cb.like(cb.lower(usuarioJoin.get("apellido")), like),
                        cb.like(cb.lower(root.get("telefono")), like)
                );
            });
        }

        if (idLider != null) {
            spec = spec.and((root, query, cb) -> {
                        Join<Persona, Usuario> usuarioJoin = root.join("usuario", JoinType.LEFT);
                        return cb.equal(usuarioJoin.get("idUsuario"), idLider);
                    }
            );
        }

        if (departamento != null) {

            spec = spec.and((root, query, cb) -> {

                Join<Persona, Usuario> usuarioJoin =
                        root.join("usuario", JoinType.LEFT);

                Join<Usuario, Ciudad> ciudadJoin =
                        usuarioJoin.join("ciudad", JoinType.LEFT);

                Join<Ciudad, Departamento> departamentoJoin =
                        ciudadJoin.join("departamento", JoinType.LEFT);

                return cb.equal(
                        departamentoJoin.get("idDepartamento"),
                        departamento
                );
            });
        }

        if (PuestoVotacion != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("idPuestoVotacion"), PuestoVotacion)
            );
        }



        if (mesa != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("id_mesa"), mesa)
            );
        }

        if (usuarioRegistro != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("idUsuarioRegistro"), usuarioRegistro)
            );
        }


        if (idCiudad != null) {
            spec = spec.and((root, query, cb) -> {

                Join<Persona, Usuario> usuarioJoin =
                        root.join("usuario", JoinType.LEFT);

                Join<Usuario, Ciudad> ciudadJoin =
                        usuarioJoin.join("ciudad", JoinType.LEFT);

                return cb.equal(
                        ciudadJoin.get("idCiudad"),
                        idCiudad
                );
            });
        }


        if(estado_votacion != null){
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("estadoVotacion"), estado_votacion)
            );
        }

        Page<Persona> personas = personaRepository.findAll(spec,pageable);
        return personas.map(this::mapToDTO);
    }

    public Page<PersonaResponseDTO> getPersonasLider(Long idLider, int page, int size, String search, Boolean estado_votacion, Long usuarioRegistro){
        Pageable pageable = PageRequest.of(page, size);
        Specification<Persona> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) -> {
                    Join<Persona, Usuario> usuarioJoin = root.join("usuario", JoinType.INNER);
                    return cb.equal(usuarioJoin.get("idUsuario"), idLider);
                }
        );

        Short currentYear = (short) Year.now().getValue();
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("year"), currentYear)
        );

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                var nombreCompleto = cb.concat(
                        cb.concat(root.get("primerNombre"), " "),
                        cb.concat(
                                cb.concat(root.get("segundoNombre"), " "),
                                cb.concat(
                                        cb.concat(root.get("primerApellido"), " "),
                                        root.get("segundoApellido")
                                )
                        )
                );

                String searchHash = DigestUtils.sha256Hex(search);
                return cb.or(
                        cb.like(cb.lower(nombreCompleto), like),
                        cb.equal(root.get("numeroIdentificacionHash"), searchHash),
                        cb.like(cb.lower(root.get("telefono")), like)
                );
            });
        }

        if (usuarioRegistro != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("idUsuarioRegistro"), usuarioRegistro)
            );
        }

        if(estado_votacion != null){
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("estadoVotacion"), estado_votacion)
            );
        }

        Page<Persona> personas = personaRepository.findAll(spec,pageable);
        return personas.map(this::mapToDTO);
    }

    public List<PersonaResponseDTO> getPersonasExport(){
        return personaRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public List<PersonaResponseDTO> getPersonasLiderExport(Long idLider) {
        Short currentYear = (short) Year.now().getValue();
        Specification<Persona> spec = (root, query, cb) -> {
            Join<Persona, Usuario> usuarioJoin = root.join("usuario", JoinType.INNER);
            return cb.and(
                    cb.equal(usuarioJoin.get("idUsuario"), idLider),
                    cb.equal(root.get("year"), currentYear)
            );
        };
        return personaRepository.findAll(spec).stream().map(this::mapToDTO).toList();
    }

    public PersonaResponseDTO getPersona(Long idPersona){
        Optional<Persona> personaOptional = personaRepository.findById(idPersona);
        if(personaOptional.isEmpty()){
            throw new RuntimeException("La persona no existe");
        }
        return mapToDTO(personaOptional.get());
    }

    public PersonaStatsResponseDTO getStats(Long idLider) {
        LocalDate hoy = LocalDate.now();
        Short year = (short) Year.now().getValue();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59, 999_999_999);

        int totalPersonas;
        int totalVotaron;
        int totalSinVotar;
        int registradosHoy;

            // Estadísticas filtradas por líder
            totalPersonas = personaRepository.countByUsuarioIdUsuarioAndYear(idLider, year);
            totalVotaron = personaRepository.countByUsuarioIdUsuarioAndEstadoVotacionAndYear(idLider, true, year);
            totalSinVotar = personaRepository.countByUsuarioIdUsuarioAndEstadoVotacionAndYear(idLider, false, year);
            registradosHoy = personaRepository.countByUsuarioIdUsuarioAndFechaRegistroBetween(idLider, inicio, fin);

        double progresoVotacion = totalPersonas > 0 ? (double) totalVotaron / totalPersonas * 100 : 0;

        PersonaStatsResponseDTO stats = new PersonaStatsResponseDTO();
        stats.setTotalPersonas(totalPersonas);
        stats.setTotalVotaron(totalVotaron);
        stats.setTotalSinVotar(totalSinVotar);
        stats.setProgresoVotacion(progresoVotacion);
        stats.setRegistradosHoy(registradosHoy);
        return stats;
    }

    public ResponseDTO crear(String correo, PersonaRequestDTO data, MultipartFile image, HttpServletRequest request) throws Exception {
        Short anioActual = (short) Year.now().getValue();
        String numeroIdentificacionHash = DigestUtils.sha256Hex(data.getNumero_identificacion());

        Optional<Persona> personaExistente = personaRepository.findByNumeroIdentificacionHashAndYear(numeroIdentificacionHash, anioActual);

        Optional<PuestoVotacion> puestoVotacionOptional = puestoVotacionRepository.findById(data.getId_puesto_votacion());
        if (puestoVotacionOptional.isEmpty()) {

            throw new RuntimeException("El puesto de votacion no existe");
        }

        Optional<Mesa> mesaOptional = mesaRepository.findById(data.getId_mesa());

        if (mesaOptional.isEmpty()) {
            throw new RuntimeException("La mesa especificada no existe");
        }
        if(personaExistente.isPresent()){
            auditoriaService.saveAuditoria(correo, "Se intento registrar a " + personaExistente.get().getPrimerNombre() + " " + personaExistente.get().getPrimerApellido() + " Con numero de cedula " + AESUtils.decrypt(personaExistente.get().getNumeroIdentificacion()) + " del lider " + personaExistente.get().getUsuario().getNombre() + " " + personaExistente.get().getUsuario().getApellido() + " de " + personaExistente.get().getUsuario().getCiudad().getNombreCiudad() + ", " + personaExistente.get().getUsuario().getCiudad().getDepartamento().getNombreDepartamento());
            throw new RuntimeException("Esta persona ya esta registrada en el sistema");
        }

        // Validar que el líder existe
        if(data.getId_lider() != null) {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(data.getId_lider());
            if(usuarioOptional.isEmpty()) {
                throw new RuntimeException("El líder especificado no existe");
            }
        }

        Optional<Usuario> liderOrSubliderOptional =
                usuarioRepository.findByCredencial_CorreoAndRol_NombreRolIn(
                        correo, List.of("LÍDER", "SUBLÍDER")
                );

        if(liderOrSubliderOptional.isEmpty()) {
           throw new RuntimeException("Este usuario no existe");
        }

        Persona persona = new Persona();
        persona.setPrimerNombre(data.getPrimer_nombre());
        persona.setSegundoNombre(data.getSegundo_nombre());
        persona.setPrimerApellido(data.getPrimer_apellido());
        persona.setSegundoApellido(data.getSegundo_apellido());
        persona.setNumeroIdentificacion(AESUtils.encrypt(data.getNumero_identificacion()));
        persona.setNumeroIdentificacionHash(DigestUtils.sha256Hex(data.getNumero_identificacion()));
        persona.setIdPuestoVotacion(data.getId_puesto_votacion());
        persona.setId_mesa(mesaOptional.get().getIdMesa());
        persona.setEstadoVotacion(data.getEstado_votacion());
        persona.setTelefono(data.getTelefono());
        persona.setIdUsuarioRegistro(liderOrSubliderOptional.get().getIdUsuario());
        persona.setFechaRegistro(LocalDateTime.now());
        persona.setYear(anioActual);

        if(data.getId_lider() != null) {
            Usuario usuario = usuarioRepository.findById(data.getId_lider()).get();
            persona.setUsuario(usuario);
        }

        String imagen_url = imagesService.guardarImagen(image);
        persona.setUrlImagen(imagen_url);

        personaRepository.save(persona);
        auditoriaService.saveAuditoria(correo, "Se ha añadido una nueva persona al sistema");
        return getresponseDTO("Persona creada correctamente", 201, request);
    }

    public ResponseDTO actualizar(String correo, Long idPersona, PersonaRequestDTO data, MultipartFile image, HttpServletRequest request) throws Exception {
        Optional<Persona> personaOptional = personaRepository.findById(idPersona);
        if(personaOptional.isEmpty()){
            throw new RuntimeException("La persona no existe");
        }

        String numeroIdentificacionHash = DigestUtils.sha256Hex(data.getNumero_identificacion());
        Optional<Persona> personaExistente = personaRepository.findByNumeroIdentificacionHashAndYearAndIdPersonaNot(
                numeroIdentificacionHash,
                personaOptional.get().getYear(),
                idPersona
        );
        if (personaExistente.isPresent()) {
            throw new RuntimeException("Ya existe una persona con este número de identificación");
        }

        // Validar que el líder existe si se proporciona
        if(data.getId_lider() != null) {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(data.getId_lider());
            if(usuarioOptional.isEmpty()) {
                throw new RuntimeException("El líder especificado no existe");
            }
        }

        Persona persona = personaOptional.get();
        persona.setPrimerNombre(data.getPrimer_nombre());
        persona.setSegundoNombre(data.getSegundo_nombre());
        persona.setPrimerApellido(data.getPrimer_apellido());
        persona.setSegundoApellido(data.getSegundo_apellido());
        persona.setNumeroIdentificacion(AESUtils.encrypt(data.getNumero_identificacion()));
        persona.setNumeroIdentificacionHash(numeroIdentificacionHash);
        persona.setEstadoVotacion(data.getEstado_votacion());
        persona.setTelefono(data.getTelefono());

        if(data.getId_lider() != null) {
            Usuario usuario = usuarioRepository.findById(data.getId_lider()).get();
            persona.setUsuario(usuario);
        }

        if (image != null) {
            imagesService.eliminarImagen(persona.getUrlImagen());
            persona.setUrlImagen(imagesService.guardarImagen(image));
        }

        personaRepository.save(persona);
        auditoriaService.saveAuditoria(correo, "Se ha actualizado una persona del sistema");
        return getresponseDTO("Persona actualizada correctamente", 200, request);
    }

    private ResponseDTO getresponseDTO(String message, int status, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(dateFormat.getDate());
        responseDTO.setMessage(message);
        responseDTO.setStatus(status);
        responseDTO.setUrl(request.getRequestURI().replace(request.getContextPath(), ""));
        return responseDTO;
    }

    private PersonaResponseDTO mapToDTO(Persona personaData) {
        PersonaResponseDTO persona = new PersonaResponseDTO();
        persona.setId_persona(personaData.getIdPersona());
        persona.setPrimer_nombre(personaData.getPrimerNombre());
        persona.setSegundo_nombre(personaData.getSegundoNombre());
        persona.setPrimer_apellido(personaData.getPrimerApellido());
        persona.setSegundo_apellido(personaData.getSegundoApellido());

        try {
            String numeroDescifrado = AESUtils.decrypt(personaData.getNumeroIdentificacion());
            persona.setNumero_identificacion(numeroDescifrado);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo descifrar el numero de identificacion");
        }
        Optional<PuestoVotacion> puestoVotacionOptional = puestoVotacionRepository.findById(personaData.getIdPuestoVotacion());
        if(puestoVotacionOptional.isEmpty()){
            throw new RuntimeException("El puesto de votacion no existe");
        }
        Optional<Mesa> mesaOptional = mesaRepository.findById(personaData.getId_mesa());
        if(mesaOptional.isEmpty()){
            throw new RuntimeException("La mesa especificada no existe");
        }
        persona.setMesa(mesaOptional.get().getNumeroMesa());
        persona.setLugar_votacion(puestoVotacionOptional.get().getNombrePuesto());
        persona.setId_mesa(mesaOptional.get().getIdMesa());
        persona.setId_puesto_votacion(puestoVotacionOptional.get().getIdPuestoVotacion());
        persona.setTelefono(personaData.getTelefono());
        persona.setEstado_votacion(personaData.getEstadoVotacion());
        persona.setFecha(personaData.getFechaRegistro().toString());
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(personaData.getIdUsuarioRegistro());
        persona.setUsuario_registro(usuarioOptional.get().getNombre() + " " + usuarioOptional.get().getApellido());
        persona.setImagen_url(
                personaData.getUrlImagen() != null
                        ? "/api/v1/images/" + personaData.getUrlImagen()
                        : null
        );
        persona.setYear(personaData.getYear());

        // Incluir información del líder si existe
        if(personaData.getUsuario() != null) {
            persona.setId_lider(personaData.getUsuario().getIdUsuario());
            persona.setNombre_lider(personaData.getUsuario().getNombre() + " " + personaData.getUsuario().getApellido());
        }
        if (personaData.getUsuario() != null) {

            Ciudad ciudad = personaData.getUsuario().getCiudad();

            if (ciudad != null) {
                persona.setCiudad(ciudad.getNombreCiudad());

                Departamento departamento = ciudad.getDepartamento();
                if (departamento != null) {
                    persona.setDepartamento(departamento.getNombreDepartamento());
                } else {
                    persona.setDepartamento(null);
                }

            } else {
                persona.setCiudad(null);
                persona.setDepartamento(null);
            }
        }


        return persona;
    }
}