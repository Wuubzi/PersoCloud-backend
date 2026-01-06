package com.app.demo.Services;

import com.app.demo.DTO.Request.DepartamentoRequestDTO;
import com.app.demo.DTO.Response.DepartamentoResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Departamento;
import com.app.demo.Repositories.DepartamentoRepository;
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
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final DateFormat dateFormat;

    @Autowired
    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               DateFormat dateFormat) {
        this.departamentoRepository = departamentoRepository;
        this.dateFormat = dateFormat;
    }

    public Page<DepartamentoResponseDTO> getDepartamentos(int page, int size, String search){
        Pageable pageable = PageRequest.of(page, size);

        Specification<Departamento> spec = (root, query, cb) -> cb.conjunction();
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";

            spec = spec.and((root, query, cb) -> {

              return cb.like(cb.lower(root.get("nombreDepartamento")), like);
            });
        }
        Page<Departamento> departamentos = departamentoRepository.findAll(spec,pageable);

        return departamentos.map(this::mapToDTO);
    }

    public List<DepartamentoResponseDTO> getDepartamentosExport(){
        return departamentoRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public DepartamentoResponseDTO getDepartamento(Long idDepartamento){
        Optional<Departamento> departamentoOptional = departamentoRepository.findById(idDepartamento);
        if(departamentoOptional.isEmpty()){
            throw new RuntimeException("El departamento no existe");
        }
        return mapToDTO(departamentoOptional.get());
    }



    public ResponseDTO crearDepartamento(DepartamentoRequestDTO data, HttpServletRequest request){
        Departamento  departamento = new Departamento();
        departamento.setNombreDepartamento(data.getNombre_departamento());
        departamentoRepository.save(departamento);
        return getresponseDTO("Departamento creado exitosamente", 200, request);
    }

    public ResponseDTO actualizarDepartamento(Long idDepartamento, DepartamentoRequestDTO data, HttpServletRequest request){
        Optional<Departamento> departamentoOptional = departamentoRepository.findById(idDepartamento);
        if(departamentoOptional.isEmpty()){
            throw new RuntimeException("El departamento no existe");
        }
        Departamento departamento = departamentoOptional.get();
        departamento.setNombreDepartamento(data.getNombre_departamento());
        departamentoRepository.save(departamento);
        return getresponseDTO("Departamento actualizado exitosamente", 200, request);
    }


    private DepartamentoResponseDTO mapToDTO(Departamento departamentoData) {
        DepartamentoResponseDTO departamento = new DepartamentoResponseDTO();
        departamento.setId_departamento(departamentoData.getIdDepartamento());
        departamento.setNombre(departamentoData.getNombreDepartamento());
        return departamento;
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
