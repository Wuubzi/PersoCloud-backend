package com.app.demo.Services;

import com.app.demo.DTO.Request.BarrioRequestDTO;
import com.app.demo.DTO.Response.BarrioResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Auditoria;
import com.app.demo.Models.Barrio;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.BarrioRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BarrioService {

    private final BarrioRepository barrioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final DateFormat dateFormat;

    @Autowired
    public BarrioService(BarrioRepository barrioRepository,
                         UsuarioRepository usuarioRepository,
                         AuditoriaService auditoriaService,
                         DateFormat dateFormat) {
        this.barrioRepository = barrioRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.dateFormat = dateFormat;
    }

    public Page<BarrioResponseDTO> getBarrios(int page, int size, String search){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Barrio> spec = (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("nombreBarrio")), like)
                    )
            );
        }

        Page<Barrio> barrios = barrioRepository.findAll(spec,pageable);

        return barrios.map(this::mapToDTO);
    }

    public BarrioResponseDTO getBarrio(Long idBarrio){
        Optional<Barrio> barrioOptional = barrioRepository.findById(idBarrio);
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }

        return mapToDTO(barrioOptional.get());
    }

    public BarrioResponseDTO getBarrioLider(Long idLider) {
        Optional<Barrio> barrioOptional = barrioRepository.findBarrioByIdLider(idLider);
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }

        return mapToDTO(barrioOptional.get());
    }

    public ResponseDTO crearBarrio(String correoUsuario,BarrioRequestDTO barrioRequestDTO, HttpServletRequest request){
        Optional<Barrio> barrioOptional = barrioRepository.findBarrioByNombreBarrio(barrioRequestDTO.getNombre_barrio());
        Optional<Usuario> liderOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(barrioRequestDTO.getId_lider(), "LÍDER");
        if (barrioOptional.isPresent()) {
            throw new RuntimeException("El barrio ya existe");
        }
        if (liderOptional.isEmpty()) {
            throw new EntityNotFoundException("El lider no existe");
        }

        Usuario lider = liderOptional.get();

        Barrio barrio = new Barrio();
        barrio.setNombreBarrio(barrioRequestDTO.getNombre_barrio());
        barrio.setIdLider(lider.getIdUsuario());
        barrio.setEstado(true);
        barrioRepository.save(barrio);
        this.auditoriaService.saveAuditoria(correoUsuario, "Nuevo barrio añadido");
        return getresponseDTO("Barrio Creado Exitosamente", 200, request);
    }

    public ResponseDTO actualizarBarrio(String correoUsuario, Long idBarrio, BarrioRequestDTO barrioRequestDTO, HttpServletRequest request){
        Optional<Barrio> barrioOptional = barrioRepository.findById(idBarrio);
        Optional<Usuario> liderOptional = usuarioRepository.findByIdUsuarioAndRol_NombreRol(barrioRequestDTO.getId_lider(), "LÍDER");
        if (barrioOptional.isEmpty()) {
            throw new EntityNotFoundException("El barrio no existe");
        }
        if (liderOptional.isEmpty()) {
            throw new EntityNotFoundException("El lider no existe");
        }

        Usuario lider = liderOptional.get();
        Barrio barrio = barrioOptional.get();
        barrio.setNombreBarrio(barrioRequestDTO.getNombre_barrio());
        barrio.setIdLider(lider.getIdUsuario());
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
        Optional<Usuario> lider = usuarioRepository.findByIdUsuarioAndRol_NombreRol(barrioData.getIdLider(), "LÍDER");
         barrio.setNombre_lider(lider.get().getNombre() + " " + lider.get().getApellido());
         barrio.setId_lider(lider.get().getIdUsuario());
         barrio.setEstado(barrioData.getEstado());
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
