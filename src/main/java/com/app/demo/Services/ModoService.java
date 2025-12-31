package com.app.demo.Services;

import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.ModoSistema;
import com.app.demo.Repositories.ModoRepository;
import com.app.demo.Utils.DateFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModoService {
    private final AuditoriaService auditoriaService;
    private final ModoRepository modoRepository;
    private final DateFormat dateFormat;

    @Autowired
    public ModoService(ModoRepository modoRepository,
                       DateFormat dateFormat,
                        AuditoriaService auditoriaService) {
        this.modoRepository = modoRepository;
        this.dateFormat = dateFormat;
        this.auditoriaService = auditoriaService;
    }

    public Boolean getModo(){
        return modoRepository.findById(1L).get().getModo();
    }

    public ResponseDTO setModo(String correoUsuario, HttpServletRequest request){
        Optional<ModoSistema> modoSistemaOptional = modoRepository.findById(1L);
        if (modoSistemaOptional.isEmpty()) {
            throw new RuntimeException("El modo no existe");
        }

        ModoSistema modoSistema = modoSistemaOptional.get();

        modoSistema.setModo(!modoSistema.getModo());
        modoRepository.save(modoSistema);
        if (modoSistema.getModo()) {
            this.auditoriaService.saveAuditoria(correoUsuario, "Modo Activado ");
        } else {
            this.auditoriaService.saveAuditoria(correoUsuario, "Modo Desactivado");
        }

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Modo cambiado exitosamente");
        responseDTO.setStatus(200);
        responseDTO.setUrl(request.getRequestURI().replace(request.getContextPath(), ""));
        responseDTO.setTimestamp(dateFormat.getDate());
        return responseDTO;
    }
}
