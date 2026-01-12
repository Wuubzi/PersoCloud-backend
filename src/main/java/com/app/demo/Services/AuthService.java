package com.app.demo.Services;

import com.app.demo.DTO.Request.LoginRequestDTO;
import com.app.demo.DTO.Response.AuthResponseDTO;
import com.app.demo.Models.Ciudad;
import com.app.demo.Models.ModoSistema;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.*;
import com.app.demo.Utils.CustomUserDetails;
import com.app.demo.Utils.DateFormat;
import com.app.demo.Utils.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private ModoRepository modoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CiudadRepository ciudadRepository;
    private final RolRepository rolRepository;
    private final UsuarioHelperService usuarioHelperService;
    private final CredencialRepository credencialRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final DateFormat dateFormat;

    @Autowired
    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       CredencialRepository credencialRepository,
                       CiudadRepository ciudadRepository,
                       UsuarioHelperService usuarioHelperService,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       ModoRepository modoRepository,
                       DateFormat dateFormat) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.credencialRepository = credencialRepository;
        this.dateFormat = dateFormat;
        this.modoRepository = modoRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usuarioHelperService = usuarioHelperService;
        this.ciudadRepository = ciudadRepository;
    }

  public AuthResponseDTO login(LoginRequestDTO data, HttpServletRequest request) {
      return getAuthResponseDTO(data, request);

  }

  public  AuthResponseDTO loginApp(LoginRequestDTO data, HttpServletRequest request) {
        Optional<ModoSistema> modoSistemaOptional = modoRepository.findById(1L);
        if (modoSistemaOptional.isEmpty()) {
            throw new RuntimeException("El modo no existe");
        }
        if (!modoSistemaOptional.get().getModo()) {
            throw new RuntimeException("No Tienes Acceso al Sistema");
        }

       String correo = this.usuarioHelperService.obtenerCorreoLiderPrincipal(data.getCorreo());
      Usuario usuario = usuarioRepository.findUsuarioByCredencial_Correo(correo)
              .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

      if (usuario.getCiudad() == null) {
          throw new RuntimeException("El líder o sublíder no tiene ciudad asignada");
      }
      return getAuthResponseDTO(data, request);
  }


    @NonNull
    private AuthResponseDTO getAuthResponseDTO(LoginRequestDTO data, HttpServletRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findUsuarioByCredencial_Correo(data.getCorreo());
        if (usuario.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        boolean isPasswordMatch = passwordEncoder.matches(data.getContrasena(), usuario.get().getCredencial().getContrasena());
        if (!isPasswordMatch) {
            throw new RuntimeException("La contraseña es incorrecta");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        data.getCorreo(),
                        data.getContrasena()
                )
        );
        CustomUserDetails userDetails = new CustomUserDetails(usuario.get());
        String token = jwtService.generateToken(userDetails);
        return getresponseDTO("Login exitoso", 200, request, token);
    }

    public String hash(String password) {
        return passwordEncoder.encode(password);
  }

  private AuthResponseDTO getresponseDTO(String message, int status, HttpServletRequest request, String token) {
      AuthResponseDTO responseDTO = new AuthResponseDTO();
      responseDTO.setTimestamp(dateFormat.getDate());
      responseDTO.setMessage(message);
      responseDTO.setToken(token);
      responseDTO.setStatus(status);
      responseDTO.setUrl(request.getRequestURI().replace(request.getContextPath(), ""));
      return responseDTO;
  }
}
