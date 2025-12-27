package com.app.demo.Utils;

import com.app.demo.Models.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (usuario.getRol() != null) {
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return usuario.getCredencial().getContrasena();
    }

    @Override
    public String getUsername() {
        return String.valueOf(usuario.getCredencial().getCorreo());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}