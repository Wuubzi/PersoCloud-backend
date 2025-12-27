package com.app.demo.Services;

import com.app.demo.DTO.Request.LoginRequestDTO;
import com.app.demo.DTO.Response.AuthResponseDTO;
import com.app.demo.DTO.Response.ResponseDTO;
import com.app.demo.Models.Usuario;
import com.app.demo.Repositories.CredencialRepository;
import com.app.demo.Repositories.RolRepository;
import com.app.demo.Repositories.UsuarioRepository;
import com.app.demo.Utils.CustomUserDetails;
import com.app.demo.Utils.DateFormat;
import com.app.demo.Utils.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final CredencialRepository credencialRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final DateFormat dateFormat;

    @Autowired
    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       CredencialRepository credencialRepository,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       DateFormat dateFormat) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.credencialRepository = credencialRepository;
        this.dateFormat = dateFormat;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

  public AuthResponseDTO login(LoginRequestDTO data, HttpServletRequest request) {
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
