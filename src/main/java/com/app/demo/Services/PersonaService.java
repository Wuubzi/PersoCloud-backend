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
import com.app.demo.Utils.AESUtils;
import com.app.demo.Utils.DateFormat;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;
    private final BarrioRepository barrioRepository;
    private final AuditoriaService auditoriaService;
    private final DateFormat dateFormat;

    @Autowired
    public PersonaService(PersonaRepository personaRepository,
                          AuditoriaService auditoriaService,
                          BarrioRepository barrioRepository,
                          DateFormat dateFormat) {
        this.personaRepository = personaRepository;
        this.barrioRepository = barrioRepository;
        this.dateFormat = dateFormat;
        this.auditoriaService = auditoriaService;
    }


    public Page<PersonaResponseDTO> getPersonas(int page, int size, String search, Boolean estado_votacion){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Persona> spec = (root, query, cb) -> cb.conjunction();


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
                    String searchHash = DigestUtils.sha256Hex(search);

                    return cb.or(
                            cb.like(cb.lower(nombreCompleto), like),          // Nombres
                            cb.equal(root.get("numeroIdentificacionHash"), searchHash), // Identificación cifrada (búsqueda exacta)
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

    public PersonaStatsResponseDTO getStats(Long idBarrio) {
        LocalDate hoy = LocalDate.now();

        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59, 999_999_999);
        int totalPersonas = personaRepository.countAllByIdBarrio(idBarrio);
        int totalVotaron = personaRepository.countAllByIdBarrioAndEstadoVotacion(idBarrio, true);
        int totalSinVotar = personaRepository.countAllByIdBarrioAndEstadoVotacion(idBarrio, false);
        int registradosHoy = personaRepository.findByFechaRegistroBetween(inicio,fin).size();
        double progresoVotacion = (double) totalVotaron / totalPersonas * 100;
        PersonaStatsResponseDTO stats = new PersonaStatsResponseDTO();
        stats.setTotalPersonas(totalPersonas);
        stats.setTotalVotaron(totalVotaron);
        stats.setTotalSinVotar(totalSinVotar);
        stats.setProgresoVotacion(progresoVotacion);
        stats.setRegistradosHoy(registradosHoy);
        return stats;
    }

    public ResponseDTO crear(String correo, PersonaRequestDTO data, HttpServletRequest request) throws Exception {
        Optional<Persona> personaOptional = personaRepository.findById(data.getId_barrio());
        Persona persona = new Persona();
        persona.setPrimerNombre(data.getPrimer_nombre());
        persona.setSegundoNombre(data.getSegundo_nombre());
        persona.setPrimerApellido(data.getPrimer_apellido());
        persona.setSegundoApellido(data.getSegundo_apellido());
        persona.setNumeroIdentificacion( AESUtils.encrypt(data.getNumero_identificacion()));
        persona.setNumeroIdentificacionHash(DigestUtils.sha256Hex(data.getNumero_identificacion()));
        persona.setEstadoVotacion(data.getEstado_votacion());
        persona.setTelefono(data.getTelefono());
        persona.setIdBarrio(data.getId_barrio());
        personaRepository.save(persona);
        auditoriaService.saveAuditoria(correo, "Se ha añadido una nueva persona al sistema");

        return getresponseDTO("Persona creada correctamente", 201, request);
    }

    public ResponseDTO actualizar(String correo, Long idPersona, PersonaRequestDTO data, HttpServletRequest request) throws Exception {
        Optional<Persona> personaOptional = personaRepository.findById(idPersona);
        if(personaOptional.isEmpty()){
            throw new RuntimeException("La persona no existe");
        }
        Persona persona = personaOptional.get();
        persona.setPrimerNombre(data.getPrimer_nombre());
        persona.setSegundoNombre(data.getSegundo_nombre());
        persona.setPrimerApellido(data.getPrimer_apellido());
        persona.setSegundoApellido(data.getSegundo_apellido());
        persona.setNumeroIdentificacion( AESUtils.encrypt(data.getNumero_identificacion()));
        persona.setEstadoVotacion(data.getEstado_votacion());
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
        persona.setNombre(personaData.getPrimerNombre() + " " + personaData.getSegundoNombre() + " " + personaData.getPrimerApellido() + " " + personaData.getSegundoApellido());
        // Descifrar numeroIdentificacion antes de devolverlo
        try {
            String numeroDescifrado = AESUtils.decrypt(personaData.getNumeroIdentificacion());
            persona.setNumero_identificacion(numeroDescifrado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
         throw new RuntimeException("No se pudo descifrar el numero de identificacion");

        }
        persona.setTelefono(personaData.getTelefono());
        persona.setEstado_votacion(personaData.getEstadoVotacion());
        Optional<Barrio> barrioOptional = barrioRepository.findById(personaData.getIdBarrio());
        if (barrioOptional.isEmpty()) {
            throw new RuntimeException("El barrio no existe");
        }
        persona.setBarrio_nombre(barrioOptional.get().getNombreBarrio());
        return persona;
    }
}
