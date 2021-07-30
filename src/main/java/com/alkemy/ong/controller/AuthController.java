package com.alkemy.ong.controller;

import javax.validation.Valid;

import com.alkemy.ong.dto.request.LoginUsersDto;
import com.alkemy.ong.dto.response.UserResponseDto;
import com.alkemy.ong.exception.EmailAlreadyRegistered;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.alkemy.ong.dto.request.UsersRequestDto;
import com.alkemy.ong.service.Interface.IUser;

@Api(value = "Auth controller")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUser usersService;
    private final ProjectionFactory projectionFactory;

    @Autowired
    public AuthController(IUser usersService, ProjectionFactory projectionFactory) {
        this.usersService = usersService;
        this.projectionFactory = projectionFactory;
    }

    @PostMapping(path = "/register")
    @ApiOperation("Registro de usuarios.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuario registrado con  éxito."),
            @ApiResponse(code = 400, message = "Error no se pudo realizar el registro."),
            @ApiResponse(code = 409, message = "El email ya se encuentra registrado.")
    })
    public ResponseEntity<?> createUser(@Valid @ModelAttribute(name = "usersCreationDto") UsersRequestDto usersRequestDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(usersService.createUser(usersRequestDto));
        } catch (EmailAlreadyRegistered e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(path = "/login")
    @ApiOperation("Inicio de sesion.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Inicio de sesión exitosa."),
            @ApiResponse(code = 401, message = "El usuario o la contraseña no son correctas.")
    })
    public ResponseEntity<String> loginUser(@RequestBody LoginUsersDto credentials){
        try {
            return ResponseEntity.ok(usersService.loginUser(credentials));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(path = "/me")
    @ApiOperation("Buscar mi sesion.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Operación exitosa."),
            @ApiResponse(code = 401, message = "Error de sesión")
    })
    public ResponseEntity<Object> userInfo(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.status(HttpStatus.OK)
            		.body(projectionFactory.createProjection(UserResponseDto.class, usersService.getUser(authentication.getName())));
        }catch(UsernameNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
