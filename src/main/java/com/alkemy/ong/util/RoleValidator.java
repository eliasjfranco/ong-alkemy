package com.alkemy.ong.util;

import com.alkemy.ong.exception.InvalidUserException;
import com.alkemy.ong.model.User;
import com.alkemy.ong.service.impl.UserServiceImpl;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;
import java.util.Objects;
/*
@Component
@AllArgsConstructor
public class RoleValidator {

    private final UserServiceImpl usersService;
    private final MessageSource messageSource;

    public boolean isAuthorized() throws InvalidUserException {
        if(isAdmin())
            return true;
        Long idFromToken = Long.valueOf(getIdFromToken(getToken()));
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = ((UserDetails) principal).getUsername();
        UserDetails currentUser = usersService.loadUserByUsername(currentUsername);
        if(currentUser.getId().equals(idFromToken))
            return true;
        else
            throw new InvalidUserException(messageSource.getMessage("user.error.invalid.user", null, Locale.getDefault()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    private boolean isAdmin(){
        return true;
    }


    public String getIdFromToken(String token) {
        return Jwts.parser().setSigningKey("${jwt.secret}").parseClaimsJws(token).getBody().getId();
    }

    public String getToken(){
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization");
    }


}*/
