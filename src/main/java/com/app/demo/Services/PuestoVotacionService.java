package com.app.demo.Services;

import com.app.demo.DTO.Request.PuestoVotacionRequestDTO;
import com.app.demo.DTO.Response.PuestoVotacionResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Ciudad;
import com.app.demo.Models.PuestoVotacion;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.CiudadRepository;
import com.app.demo.Repositories.PuestoVotacionRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.DateFormat;
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
public class PuestoVotacionService {

    private final PuestoVotacionRepository puestoVotacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final DateFormat dateFormat;

    @Autowired
    public PuestoVotacionService(
            PuestoVotacionRepository puestoVotacionRepository,
            DateFormat dateFormat,
            UsuarioRepository usuarioRepository
    ) {
        this.puestoVotacionRepository = puestoVotacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.dateFormat = dateFormat;
    }

    public Page<PuestoVotacionResponseDTO> PuestosVotacion(String correoLider,int page,int size, String search){
        Pageable pageable = PageRequest.of(page, size);


        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }

        Usuario usuario = optionalUsuario.get();
        Ciudad ciudad = usuario.getCiudad();


        Specification<PuestoVotacion> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("ciudad").get("idCiudad"), ciudad.getIdCiudad())
        );

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombrePuesto")), like));
        }

        Page<PuestoVotacion> puestoVotaciones = puestoVotacionRepository.findAll(spec,pageable);

        return puestoVotaciones.map(this::mapToDTO);
    }

    public List<PuestoVotacionResponseDTO> getPuestosVotacionByCiudad(Long idCiudad){
        return puestoVotacionRepository.findAllByCiudad_IdCiudad(idCiudad).stream().map(this::mapToDTO).toList();
    }

    public List<PuestoVotacionResponseDTO> getPuestosVotacionExport(String correoLider){
        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }

        Usuario usuario = optionalUsuario.get();
        Ciudad ciudad = usuario.getCiudad();
        return puestoVotacionRepository.findAllByCiudad_IdCiudad(ciudad.getIdCiudad()).stream().map(this::mapToDTO).toList();
    }

    public ResponseDTO create(String correoLider, PuestoVotacionRequestDTO data, HttpServletRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }

        Usuario usuario = optionalUsuario.get();
        Ciudad ciudad = usuario.getCiudad();
        PuestoVotacion puestoVotacion = new PuestoVotacion();
        puestoVotacion.setNombrePuesto(data.getNombre());
        puestoVotacion.setCiudad(ciudad);
        puestoVotacionRepository.save(puestoVotacion);
        return getresponseDTO("Puesto de votacion creado exitosamente", 201, request);
    }

    public ResponseDTO update(String correoLider, Long idPuestoVotacion, PuestoVotacionRequestDTO data, HttpServletRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }
        Optional<PuestoVotacion> optionalPuestoVotacion = puestoVotacionRepository.findById(idPuestoVotacion);
        if (optionalPuestoVotacion.isEmpty()) {
            throw new RuntimeException("Este puesto de votacion no existe");
        }

        Usuario usuario = optionalUsuario.get();
        Ciudad ciudad = usuario.getCiudad();
        PuestoVotacion puestoVotacion = optionalPuestoVotacion.get();
        puestoVotacion.setNombrePuesto(data.getNombre());
        puestoVotacion.setCiudad(ciudad);
        puestoVotacionRepository.save(puestoVotacion);
        return getresponseDTO("Puesto de votacion actualizado exitosamente", 200, request);
    }





    private PuestoVotacionResponseDTO mapToDTO(PuestoVotacion puestoVotacion){
        PuestoVotacionResponseDTO puestoVotacionResponseDTO = new PuestoVotacionResponseDTO();
        puestoVotacionResponseDTO.setId_puesto_votacion(puestoVotacion.getIdPuestoVotacion());
        puestoVotacionResponseDTO.setNombre(puestoVotacion.getNombrePuesto());
        puestoVotacionResponseDTO.setId_ciudad(puestoVotacion.getCiudad().getIdCiudad());
        puestoVotacionResponseDTO.setCiudad(puestoVotacion.getCiudad().getNombreCiudad());
        return puestoVotacionResponseDTO;
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
