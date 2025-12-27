package com.app.demo.Services;

import com.app.demo.DTO.Response.CoordinadorResponseDTO;
import com.app.demo.DTO.Response.StatsDashboardResponseDTO;
import com.app.demo.DTO.Response.SummaryResponseDTO;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.BarrioRepository;
import com.app.demo.Repositories.PersonaRepository;
import com.app.demo.Repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoordinadorService {
    private final BarrioRepository barrioRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    @Autowired
    public CoordinadorService(BarrioRepository barrioRepository, PersonaRepository personaRepository, UsuarioRepository usuarioRepository) {
        this.barrioRepository = barrioRepository;
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CoordinadorResponseDTO getCoordinador(String correo){
        Optional<Usuario> optionalUsuario =  usuarioRepository.findByCredencial_CorreoAndRol_NombreRol(correo, "COORDINADOR");
        if (optionalUsuario.isEmpty()) {
            throw new EntityNotFoundException("El usuario no existe");
        }
        CoordinadorResponseDTO coordinador = new CoordinadorResponseDTO();
        coordinador.setNombre(optionalUsuario.get().getNombre());
        coordinador.setApellido(optionalUsuario.get().getApellido());
        coordinador.setCorreo(optionalUsuario.get().getCredencial().getCorreo());
        return coordinador;
    }

    public StatsDashboardResponseDTO getStatsDashboard(){
        int totalBarrios = barrioRepository.findAll().size();
        int totalPersonas = personaRepository.findAll().size();
        int totalLideres = usuarioRepository.findAllByRol_NombreRol("LÍDER").size();
        StatsDashboardResponseDTO stats = new StatsDashboardResponseDTO();
        stats.setTotalbarrios(totalBarrios);
        stats.setTotalPersonas(totalPersonas);
        stats.setTotalLideres(totalLideres);
        return stats;
    }

    public SummaryResponseDTO summary(){
        int personasVotadas = personaRepository.countAllByEstadoVotacion(true);
        int personasSinVotar = personaRepository.countAllByEstadoVotacion(false);
        int lideresActivos = usuarioRepository.findAllByRol_NombreRolAndEstado("LÍDER", true).size();
     SummaryResponseDTO summary = new SummaryResponseDTO();
     summary.setTotalPersonasVotadas(personasVotadas);
     summary.setTotalPersonasSinVotar(personasSinVotar);
     summary.setTotalLiderActivos(lideresActivos);
     return summary;
    }
}
