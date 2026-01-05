package com.app.demo.Services;

import com.app.demo.DTO.Request.PersonaRequestDTO;
import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.PersonaResponseDTO;
import com.app.demo.DTO.Response.PersonaStatsResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Barrio;
import com.app.demo.Models.Persona;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.BarrioRepository;
import com.app.demo.Repositories.PersonaRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.AESUtils;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.cli.Digest;
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
    private final BarrioRepository barrioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ImagesService imagesService;
    private final AuditoriaService auditoriaService;
    private final DateFormat dateFormat;

    @Autowired
    public PersonaService(PersonaRepository personaRepository,
                          AuditoriaService auditoriaService,
                          UsuarioRepository usuarioRepository,
                          BarrioRepository barrioRepository,
                          ImagesService imagesService,
                          DateFormat dateFormat) {
        this.personaRepository = personaRepository;
        this.barrioRepository = barrioRepository;
        this.dateFormat = dateFormat;
        this.imagesService = imagesService;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }


    public Page<PersonaResponseDTO> getPersonas(int page, int size, String search, Short year, Boolean estado_votacion, Long idBarrio){
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
                Join<Persona, Barrio> barrioJoin = root.join("barrio", JoinType.LEFT);
                Join<Barrio, Usuario> usuarioJoin = barrioJoin.join("usuario", JoinType.LEFT);


                var nombreCompletoLider = cb.concat(
                        cb.concat(
                                cb.lower(usuarioJoin.get("nombre")),
                                " "
                        ),
                        cb.lower(usuarioJoin.get("apellido"))
                );


                String searchHash = DigestUtils.sha256Hex(search);

                return cb.or(
                        cb.like(cb.lower(nombreCompleto), like),          // Nombres
                        cb.equal(root.get("numeroIdentificacionHash"), searchHash), // Identificación cifrada (búsqueda exacta)
                        cb.like(cb.lower(usuarioJoin.get("nombre")), like),
                        cb.like(nombreCompletoLider, like),
                        cb.like(cb.lower(usuarioJoin.get("apellido")), like),
                        cb.like(cb.lower(root.get("telefono")), like)     // Teléfono
                );
            });
        }

        if (idBarrio != null) {

            spec = spec.and((root, query, cb) -> {
                        Join<Persona, Barrio> barrioJoin = root.join("barrio", JoinType.LEFT);
                        return cb.equal(barrioJoin.get("idBarrio"), idBarrio);
                    }
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

    public Page<PersonaResponseDTO> getPersonasBarrio(Long idBarrio,int page, int size, String search, Boolean estado_votacion){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Persona> spec = (root, query, cb) -> cb.conjunction();


        spec = spec.and((root, query, cb) -> {
                 Join<Persona, Barrio> barrioJoin = root.join("barrio", JoinType.INNER);
                 return cb.equal(barrioJoin.get("idBarrio"), idBarrio);
                }
        );

        Short currentYear = (short) Year.now().getValue();
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("year"), currentYear)
        );


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
                Join<Barrio, Usuario> usuarioJoin = root.join("usuario", JoinType.LEFT);

                var nombreCompletoLider = cb.concat(
                        cb.concat(
                                cb.lower(usuarioJoin.get("nombre")),
                                " "
                        ),
                        cb.lower(usuarioJoin.get("apellido"))
                );


                String searchHash = DigestUtils.sha256Hex(search);

                return cb.or(
                        cb.like(cb.lower(nombreCompleto), like),          // Nombres
                        cb.equal(root.get("numeroIdentificacionHash"), searchHash), // Identificación cifrada (búsqueda exacta)
                        cb.like(cb.lower(usuarioJoin.get("nombre")), like),
                        cb.like(nombreCompletoLider, like),
                        cb.like(cb.lower(usuarioJoin.get("apellido")), like),
                        cb.like(cb.lower(root.get("telefono")), like)     // Teléfono
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

    public List<PersonaResponseDTO> getPersonasExport(){
        return personaRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public List<PersonaResponseDTO> getPersonasBarrioExport(Long idBarrio) {
        return personaRepository.findAllByBarrio_IdBarrio(idBarrio).stream().map(this::mapToDTO).toList();
    }



    public PersonaResponseDTO getPersona(Long idPersona){
        Optional<Persona> personaOptional = personaRepository.findById(idPersona);
        if(personaOptional.isEmpty()){
            throw new RuntimeException("La persona no existe");
        }
        return mapToDTO(personaOptional.get());
    }
    public PersonaStatsResponseDTO getStats(Long idBarrio) {
        LocalDate hoy = LocalDate.now();
        Short year = (short) Year.now().getValue();

        LocalDateTime  inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59, 999_999_999);
        int totalPersonas = personaRepository.countAllByBarrio_IdBarrioAndYear(idBarrio, year );
        int totalVotaron = personaRepository.countAllByBarrio_IdBarrioAndEstadoVotacionAndYear(idBarrio, true, year);
        int totalSinVotar = personaRepository.countAllByBarrio_IdBarrioAndEstadoVotacionAndYear(idBarrio, false, year);
        int registradosHoy = personaRepository.findByFechaRegistroBetweenAndBarrio_IdBarrio(inicio,fin, idBarrio).size();
        double progresoVotacion = (double) totalVotaron / totalPersonas * 100;
        PersonaStatsResponseDTO stats = new PersonaStatsResponseDTO();
        stats.setTotalPersonas(totalPersonas);
        stats.setTotalVotaron(totalVotaron);
        stats.setTotalSinVotar(totalSinVotar);
        stats.setProgresoVotacion(progresoVotacion);
        stats.setRegistradosHoy(registradosHoy);
        return stats;
    }

    public ResponseDTO crear(String correo, PersonaRequestDTO data, MultipartFile image, HttpServletRequest request) throws Exception {

        Optional<Barrio> barrioOptional = barrioRepository.findById(data.getId_barrio());
        if(barrioOptional.isEmpty()){
            throw new RuntimeException("El barrio no existe");
        }
        Short anioActual = (short) Year.now().getValue();

        String numeroIdentificacionHash = DigestUtils.sha256Hex(data.getNumero_identificacion());
        Optional<Persona> personaExistente = personaRepository.findByNumeroIdentificacionHashAndYear(numeroIdentificacionHash, anioActual);
        if(personaExistente.isPresent()){
           Persona persona = personaExistente.get();
            auditoriaService.saveAuditoria(correo, "Se intento registrar una persona ya registrada en el barrio " + persona.getBarrio().getNombreBarrio() + " del Lider " + persona.getBarrio().getUsuario().getNombre() + "");
            throw new RuntimeException("Esta persona ya esta registrada en el sistema");
        }

        Persona persona = new Persona();
        persona.setPrimerNombre(data.getPrimer_nombre());
        persona.setSegundoNombre(data.getSegundo_nombre());
        persona.setPrimerApellido(data.getPrimer_apellido());
        persona.setSegundoApellido(data.getSegundo_apellido());
        persona.setNumeroIdentificacion( AESUtils.encrypt(data.getNumero_identificacion()));
        persona.setNumeroIdentificacionHash(DigestUtils.sha256Hex(data.getNumero_identificacion()));
        persona.setLugarVotacion(data.getLugar_votacion());
        persona.setEstadoVotacion(data.getEstado_votacion());
        persona.setTelefono(data.getTelefono());
        persona.setYear(anioActual);
        persona.setBarrio(barrioOptional.get());
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
        Persona persona = personaOptional.get();
        persona.setPrimerNombre(data.getPrimer_nombre());
        persona.setSegundoNombre(data.getSegundo_nombre());
        persona.setPrimerApellido(data.getPrimer_apellido())    ;
        persona.setSegundoApellido(data.getSegundo_apellido());
        persona.setNumeroIdentificacion( AESUtils.encrypt(data.getNumero_identificacion()));
        persona.setLugarVotacion(data.getLugar_votacion());
        persona.setEstadoVotacion(data.getEstado_votacion());
        if (image != null) {
            imagesService.eliminarImagen(persona.getUrlImagen());
            persona.setUrlImagen(imagesService.guardarImagen(image));
        }
        persona.setTelefono(data.getTelefono());
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
        // Descifrar numeroIdentificacion antes de devolverlo
        try {
            String numeroDescifrado = AESUtils.decrypt(personaData.getNumeroIdentificacion());
            persona.setNumero_identificacion(numeroDescifrado);
        } catch (Exception e) {

         throw new RuntimeException("No se pudo descifrar el numero de identificacion");

        }
        persona.setLugar_votacion(personaData.getLugarVotacion());
        persona.setTelefono(personaData.getTelefono());
        persona.setEstado_votacion(personaData.getEstadoVotacion());
        Optional<Barrio> barrioOptional = barrioRepository.findById(personaData.getBarrio().getIdBarrio());
        if (barrioOptional.isEmpty()) {
            throw new RuntimeException("El barrio no existe");
        }
        persona.setBarrio_nombre(barrioOptional.get().getNombreBarrio());
        Optional<Usuario> liderOptional = usuarioRepository.findUsuarioByBarrio_IdBarrio(barrioOptional.get().getIdBarrio());
        persona.setLider_nombre(barrioOptional.get().getUsuario().getNombre() + " " + barrioOptional.get().getUsuario().getApellido());
        persona.setImagen_url(
                personaData.getUrlImagen() != null
                        ? "/api/v1/images/" + personaData.getUrlImagen()
                        : null
        );
        persona.setYear(personaData.getYear());
        return persona;
    }
}
