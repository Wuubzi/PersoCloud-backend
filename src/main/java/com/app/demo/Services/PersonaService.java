package com.app.demo.Services;

import com.app.demo.DTO.Response.LiderResponseDTO;
import com.app.demo.DTO.Response.PersonaResponseDTO;
import com.app.demo.Models.Persona;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;

    @Autowired
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }


    public Page<PersonaResponseDTO> getPersonas(int page, int size, String nombre, String numero_identificacion, String telefono, Boolean estado_votacion){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Persona> spec = (root, query, cb) -> cb.conjunction();


        if (nombre != null && !nombre.isBlank()) {
            String filter = "%" + nombre.toLowerCase() + "%";

            spec = spec.and((root, query, cb) -> {
                var nombreCompleto = cb.concat(cb.concat(root.get("primerNombre"), " "),
                        cb.concat(cb.concat(root.get("segundoNombre"), " "),
                                cb.concat(cb.concat(root.get("primerApellido"), " "),
                                        root.get("segundoApellido"))));

                return cb.like(cb.lower(nombreCompleto), filter);
            });
        }


        if (numero_identificacion != null && !numero_identificacion.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("numero_identificacion")), "%" + numero_identificacion.toLowerCase() + "%")
            );
        }

        if(telefono != null && !telefono.isBlank()){
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("telefono")), telefono)
            );
        }
        if(estado_votacion != null){
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("estado_votacion"), estado_votacion)
            );
        }

        Page<Persona> personas = personaRepository.findAll(spec,pageable);

        return personas.map(this::mapToDTO);
    }

    private PersonaResponseDTO mapToDTO(Persona personaData) {
        PersonaResponseDTO persona = new PersonaResponseDTO();
        persona.setId_persona(personaData.getIdPersona());
        persona.setNombre(personaData.getPrimerNombre() + " " + personaData.getSegundoNombre() + " " + personaData.getPrimerApellido() + " " + personaData.getSegundoApellido());
        persona.setNumero_identificacion(personaData.getNumeroIdentificacion());
        persona.setTelefono(personaData.getTelefono());
        persona.setEstado_votacion(personaData.getEstadoVotacion());
        persona.setBarrio(personaData.getIdBarrio());
        return persona;
    }
}
