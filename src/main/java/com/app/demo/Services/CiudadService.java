package com.app.demo.Services;

import com.app.demo.DTO.Request.CiudadRequestDTO;
import com.app.demo.DTO.Response.CiudadResponseDTO;
import com.app.demo.DTO.Response.DepartamentoResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Barrio;
import com.app.demo.Models.Ciudad;
import com.app.demo.Models.Departamento;
import com.app.demo.Models.Persona;
import com.app.demo.Repositories.CiudadRepository;
import com.app.demo.Repositories.DepartamentoRepository;
import com.app.demo.Utils.DateFormat;
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
public class CiudadService {

    private final CiudadRepository ciudadRepository;
    private final DepartamentoRepository departamentoRepository;
    private final DateFormat dateFormat;

    @Autowired
    public CiudadService(CiudadRepository ciudadRepository,
                         DepartamentoRepository departamentoRepository,
                         DateFormat dateFormat) {
        this.ciudadRepository = ciudadRepository;
        this.departamentoRepository = departamentoRepository;
        this.dateFormat = dateFormat;
    }

    public Page<CiudadResponseDTO> getCiudades(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Ciudad> spec = (root, query, cb) -> cb.conjunction();
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";



            spec = spec.and((root, query, cb) -> {
                Join<Ciudad, Departamento> departamentoJoin = root.join("departamento", JoinType.LEFT);
                return cb.or(
                        cb.like(cb.lower(root.get("nombreCiudad")), like),
                        cb.like(cb.lower(departamentoJoin.get("nombreDepartamento")), like)
                        );
            });
        }
        Page<Ciudad> ciudades = ciudadRepository.findAll(spec,pageable);

        return ciudades.map(this::mapToDTO);
    }

    public List<CiudadResponseDTO> getCiudadesExport(){
        return ciudadRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public CiudadResponseDTO getCiudad(Long idCiudad) {
        return mapToDTO(ciudadRepository.findById(idCiudad).orElseThrow());
    }

    public ResponseDTO crearCiudad(CiudadRequestDTO data, HttpServletRequest request) {
        Optional<Departamento> departamentoOptional= departamentoRepository.findById(data.getId_departamento());
        if(departamentoOptional.isEmpty()){
            throw new RuntimeException("El departamento no existe");
        }
        Departamento departamento = departamentoOptional.get();
        Ciudad ciudad = new Ciudad();
        ciudad.setNombreCiudad(data.getNombre());
        ciudad.setDepartamento(departamento);
        ciudadRepository.save(ciudad);
        return getresponseDTO("Ciudad creada exitosamente", 200, request);
    }

    public ResponseDTO actualizarCiudad(Long idCiudad, CiudadRequestDTO data, HttpServletRequest request){
        Optional<Ciudad> ciudadOptional = ciudadRepository.findById(idCiudad);
        Optional<Departamento> departamentoOptional= departamentoRepository.findById(data.getId_departamento());
        if(ciudadOptional.isEmpty()){
            throw new RuntimeException("La ciudad no existe");
        }

        if(departamentoOptional.isEmpty()){
            throw new RuntimeException("El departamento no existe");
        }
        Departamento departamento = departamentoOptional.get();
        Ciudad ciudad = ciudadOptional.get();
        ciudad.setNombreCiudad(data.getNombre());
        ciudad.setDepartamento(departamento);
        ciudadRepository.save(ciudad);
        return getresponseDTO("Ciudad actualizada exitosamente", 200, request);
    }

    private CiudadResponseDTO mapToDTO(Ciudad ciudadData) {
        CiudadResponseDTO ciudad = new CiudadResponseDTO();
        ciudad.setId_ciudad(ciudadData.getIdCiudad());
        ciudad.setNombre(ciudadData.getNombreCiudad());
        ciudad.setDepartamento(ciudadData.getDepartamento().getNombreDepartamento());
        ciudad.setId_departamento(ciudadData.getDepartamento().getIdDepartamento());
        return ciudad;
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
