package com.app.demo.Services;

import com.app.demo.DTO.Request.BarrioRequestDTO;
import com.app.demo.DTO.Response.BarrioResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.*;
import com.app.demo.Repositories.BarrioRepository;
import com.app.demo.Repositories.CiudadRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BarrioService {

    private final BarrioRepository barrioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final CiudadRepository ciudadRepository;
    private final DateFormat dateFormat;

    @Autowired
    public BarrioService(BarrioRepository barrioRepository,
                         UsuarioRepository usuarioRepository,
                         CiudadRepository ciudadRepository,
                         AuditoriaService auditoriaService,
                         DateFormat dateFormat) {
        this.barrioRepository = barrioRepository;
        this.usuarioRepository = usuarioRepository;
        this.ciudadRepository = ciudadRepository;
        this.auditoriaService = auditoriaService;
        this.dateFormat = dateFormat;
    }

    public Page<BarrioResponseDTO> getBarrios(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Barrio> spec = (root, query, cb) -> cb.conjunction();
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";

            spec = spec.and((root, query, cb) -> {

                // JOIN con usuario
                Join<Barrio, Usuario> usuarioJoin = root.join("usuario", JoinType.LEFT);
                Join<Barrio, Departamento> departamentoJoin = root.join("ciudad", JoinType.INNER).join("departamento", JoinType.INNER);

                var nombreCompleto = cb.concat(
                        cb.concat(
                                cb.lower(usuarioJoin.get("nombre")),
                                " "
                        ),
                        cb.lower(usuarioJoin.get("apellido"))
                );
                return cb.or(
                        cb.like(cb.lower(root.get("nombreBarrio")), like),
                        cb.like(cb.lower(usuarioJoin.get("nombre")), like),
                        cb.like(nombreCompleto, like),
                        cb.like(cb.lower(departamentoJoin.get("nombreDepartamento")), like),
                        cb.like(cb.lower(root.get("ciudad").get("nombreCiudad")), like),
                        cb.like(cb.lower(usuarioJoin.get("apellido")), like)
                );
            });
        }

        Page<Barrio> barrios = barrioRepository.findAll(spec, pageable);

        return barrios.map(this::mapToDTO);
    }


    public List<BarrioResponseDTO> getBarriosExport(){
        return barrioRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public BarrioResponseDTO getBarrio(Long idBarrio){
        Optional<Barrio> barrioOptional = barrioRepository.findById(idBarrio);
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }

        return mapToDTO(barrioOptional.get());
    }

    public BarrioResponseDTO getBarrioLider(Long idLider) {
        Optional<Barrio> barrioOptional = barrioRepository.findBarrioByUsuario_IdUsuario(idLider);
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }

        return mapToDTO(barrioOptional.get());
    }

    public ResponseDTO crearBarrio(String correoUsuario,BarrioRequestDTO barrioRequestDTO, HttpServletRequest request){
        Optional<Barrio> barrioOptional = barrioRepository.findBarrioByNombreBarrio(barrioRequestDTO.getNombre_barrio());
        Optional<Usuario> liderOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(barrioRequestDTO.getId_lider(), "LÍDER");
        Optional<Barrio> usuarioOptional = barrioRepository.findBarrioByUsuario_IdUsuario(barrioRequestDTO.getId_lider());
        Optional<Ciudad> ciudadOptional = ciudadRepository.findById(barrioRequestDTO.getId_ciudad());
        if (ciudadOptional.isEmpty()) {
            throw new EntityNotFoundException("La ciudad no existe");
        }
        if (usuarioOptional.isPresent()) {
            throw new RuntimeException("El usuario ya tiene un barrio asignado");
        }
        if (barrioOptional.isPresent()) {
            throw new RuntimeException("El barrio ya existe");
        }
        if (liderOptional.isEmpty()) {
            throw new EntityNotFoundException("El lider no existe");
        }

        Usuario lider = liderOptional.get();

        Barrio barrio = new Barrio();
        barrio.setNombreBarrio(barrioRequestDTO.getNombre_barrio());
        barrio.setUsuario(lider);
        barrio.setEstado(true);
        barrio.setCiudad(ciudadOptional.get());
        barrioRepository.save(barrio);
        this.auditoriaService.saveAuditoria(correoUsuario, "Nuevo barrio añadido");
        return getresponseDTO("Barrio Creado Exitosamente", 200, request);
    }

    public ResponseDTO actualizarBarrio(String correoUsuario, Long idBarrio, BarrioRequestDTO barrioRequestDTO, HttpServletRequest request){
        Optional<Barrio> barrioOptional = barrioRepository.findById(idBarrio);
        Optional<Barrio> usuarioOptional = barrioRepository.findBarrioByUsuario_IdUsuarioAndIdBarrioNot(barrioRequestDTO.getId_lider(), idBarrio);
        Optional<Usuario> liderOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(barrioRequestDTO.getId_lider(), "LÍDER");
        Optional<Ciudad> ciudadOptional = ciudadRepository.findById(barrioRequestDTO.getId_ciudad());
        if (ciudadOptional.isEmpty()) {
            throw new EntityNotFoundException("La ciudad no existe");
        }
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }
        if (usuarioOptional.isPresent()) {
            throw new RuntimeException("El usuario ya tiene un barrio asignado");
        }
        if (liderOptional.isEmpty()) {
            throw new EntityNotFoundException("El lider no existe");
        }

        Usuario lider = liderOptional.get();
        Barrio barrio = barrioOptional.get();
        barrio.setNombreBarrio(barrioRequestDTO.getNombre_barrio());
        barrio.setUsuario(lider);
        barrio.setCiudad(ciudadOptional.get());
        Barrio saved = barrioRepository.save(barrio);
        this.auditoriaService.saveAuditoria(correoUsuario, "Barrio " + saved.getNombreBarrio() + " Actualizado");
        return getresponseDTO("Barrio Actualizado Exitosamente", 200, request);
    }

    public ResponseDTO changeState(String correoUsuario, Long idBarrio, HttpServletRequest request){
        Optional<Barrio> barrioOptional = barrioRepository.findById(idBarrio);
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }

        Barrio barrio = barrioOptional.get();
        barrio.setEstado(!barrio.getEstado());
       Barrio saved =  barrioRepository.save(barrio);
        this.auditoriaService.saveAuditoria(correoUsuario, "Estado del barrio " + saved.getNombreBarrio() + " Actualizado");
        return getresponseDTO("Estado del Barrio Actualizado Exitosamente", 200, request);
    }

    private BarrioResponseDTO mapToDTO(Barrio barrioData) {
        BarrioResponseDTO barrio = new BarrioResponseDTO();
        barrio.setId_barrio(barrioData.getIdBarrio());
        barrio.setNombre(barrioData.getNombreBarrio());
        Optional<Usuario> lider = usuarioRepository.findByIdUsuarioAndRol_NombreRol(barrioData.getUsuario().getIdUsuario(), "LÍDER");
         barrio.setNombre_lider(lider.get().getNombre() + " " + lider.get().getApellido());
         barrio.setId_lider(lider.get().getIdUsuario());
         barrio.setEstado(barrioData.getEstado());
         barrio.setCiudad(barrioData.getCiudad().getNombreCiudad());
         barrio.setId_ciudad(barrio.getId_ciudad());
         barrio.setDepartamento(barrioData.getCiudad().getDepartamento().getNombreDepartamento());
        return barrio;


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
