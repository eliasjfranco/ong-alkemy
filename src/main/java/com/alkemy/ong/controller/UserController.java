package com.alkemy.ong.controller;

import com.alkemy.ong.dto.response.UserResponseDto;
import com.alkemy.ong.service.Interface.IUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonException;
import javax.json.JsonPatch;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private final IUser iUser;

    @GetMapping
    public List<UserResponseDto> showAllUsers(){
        return iUser.showAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(iUser.deleteUser(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @ApiOperation("Edicion de usuario.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Edición exitosa."),
            @ApiResponse(code = 404, message = "Usuario no encontrado.")
    })
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody JsonPatch patch) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(iUser.patchUpdate(id, authentication, patch));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (JsonException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
