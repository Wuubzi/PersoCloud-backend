package com.app.demo.Services;

import com.app.demo.DTO.Request.MesaRequestDTO;
import com.app.demo.DTO.Response.MesaResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Ciudad;
import com.app.demo.Models.Mesa;
import com.app.demo.Models.PuestoVotacion;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.MesaRepository;
import com.app.demo.Repositories.PuestoVotacionRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.DateFormat;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    private final UsuarioRepository usuarioRepository;
    private final DateFormat dateFormat;
    private final PuestoVotacionRepository puestoVotacionRepository;
    private final MesaRepository mesaRepository;

    public MesaService(UsuarioRepository usuarioRepository,
                       DateFormat dateFormat,
                       PuestoVotacionRepository puestoVotacionRepository,
                       MesaRepository mesaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.dateFormat = dateFormat;
        this.puestoVotacionRepository = puestoVotacionRepository;
        this.mesaRepository = mesaRepository;
    }





    public Page<MesaResponseDTO> Mesas(String correoLider, int page, int size, String search, Long idPuestoVotacion) {
       Pageable pageable = PageRequest.of(page, size);
        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }

        Usuario usuario = optionalUsuario.get();
        Ciudad ciudad = usuario.getCiudad();

        Specification<Mesa> spec = (root, query, cb) -> {

            Join<Mesa, PuestoVotacion> puestoJoin =
                    root.join("puestoVotacion", JoinType.INNER);

            Join<PuestoVotacion, Ciudad> ciudadJoin =
                    puestoJoin.join("ciudad", JoinType.INNER);

            return cb.equal(ciudadJoin.get("idCiudad"), ciudad.getIdCiudad());
        };

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("numeroMesa")), like));
        }
        if (idPuestoVotacion != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("puestoVotacion").get("idPuestoVotacion"), idPuestoVotacion));
        }


        Page<Mesa> mesas = mesaRepository.findAll(spec,pageable);

        return mesas.map(this::mapToDTO);
    }

    public List<MesaResponseDTO> getMesasByPuesto(Long idPuesto) {
        Optional<PuestoVotacion> optionalPuestoVotacion = puestoVotacionRepository.findById(idPuesto);
        if (optionalPuestoVotacion.isEmpty()) {
            throw new RuntimeException("Este puesto de votacion no existe");
        }

        PuestoVotacion puestoVotacion = optionalPuestoVotacion.get();
        List<Mesa> mesas = mesaRepository.findAllByPuestoVotacion_IdPuestoVotacion(puestoVotacion.getIdPuestoVotacion());
        return mesas.stream().map(this::mapToDTO).toList();
    }



    public List<MesaResponseDTO> getMesasExport(String correoLider) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findUsuarioByCredencial_Correo(correoLider);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Este usuario no existe");
        }

        Usuario usuario = optionalUsuario.get();
        Ciudad ciudad = usuario.getCiudad();

        Specification<Mesa> spec = (root, query, cb) -> {

            Join<Mesa, PuestoVotacion> puestoJoin =
                    root.join("puestoVotacion", JoinType.INNER);

            Join<PuestoVotacion, Ciudad> ciudadJoin =
                    puestoJoin.join("ciudad", JoinType.INNER);

            return cb.equal(ciudadJoin.get("idCiudad"), ciudad.getIdCiudad());
        };

        List<Mesa> mesas = mesaRepository.findAll(spec);

       return mesas.stream().map(this::mapToDTO).toList();
    }

    public ResponseDTO crearMesa(String correoLider, MesaRequestDTO data, HttpServletRequest request) {
        System.out.println(data.getId_puesto_votacion());
        Optional<PuestoVotacion> optionalPuestoVotacion = puestoVotacionRepository.findById(data.getId_puesto_votacion());
        if (optionalPuestoVotacion.isEmpty()) {
            throw new RuntimeException("Este puesto de votacion no existe");
        }
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(data.getNumero_mesa());
        mesa.setPuestoVotacion(optionalPuestoVotacion.get());
        mesaRepository.save(mesa);
        return getresponseDTO("Mesa creada exitosamente", 200, request);
    }

    public ResponseDTO updateMesa(String correoLider, Long idMesa, MesaRequestDTO data, HttpServletRequest request) {
        Optional<Mesa> optionalMesa = mesaRepository.findById(idMesa);
        Optional<PuestoVotacion> optionalPuestoVotacion = puestoVotacionRepository.findById(data.getId_puesto_votacion());
        if (optionalMesa.isEmpty()) {
            throw new RuntimeException("Esta mesa no existe");
        }
        Mesa mesa = optionalMesa.get();
        mesa.setNumeroMesa(data.getNumero_mesa());
        mesa.setPuestoVotacion(optionalPuestoVotacion.get());
        mesaRepository.save(mesa);
        return getresponseDTO("Mesa actualizada exitosamente", 200, request);
    };

    private MesaResponseDTO mapToDTO(Mesa data) {
        MesaResponseDTO mesa = new MesaResponseDTO();
        mesa.setId_mesa(data.getIdMesa());
        mesa.setNumero_mesa(data.getNumeroMesa());
        mesa.setId_puesto_votacion(data.getPuestoVotacion().getIdPuestoVotacion());
        mesa.setNombre_puesto(data.getPuestoVotacion().getNombrePuesto());
        return mesa;
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
