package com.alkemy.ong.controller;

import com.alkemy.ong.dto.request.MemberRequestDto;
import com.alkemy.ong.service.Interface.IMember;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Locale;


@RestController
@RequestMapping("/members")
@Api(value = "Miembros controller")
public class MemberController {

    private final IMember iMember;
    private final MessageSource messageSource;

    @Autowired
    public MemberController(IMember iMember, MessageSource messageSource) {
        this.iMember = iMember;
        this.messageSource = messageSource;
    }


    @GetMapping
    @ApiOperation("Obtiene todos los miembros")
    @ApiResponses({
          @ApiResponse(code = 200, message = "Operación exitosa"),
          @ApiResponse(code = 404, message = "Solicitud Incorrecta")
        })
    public ResponseEntity<?> getAllMembers(@PageableDefault(size = 10, page = 0) Pageable pageable, @RequestParam(value = "page", defaultValue = "0") int page){
    	try {
    		Page<?> result = iMember.showAllMembers(pageable);
    		if(page >= result.getTotalPages()) {
    			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("pagination.error.notFound", null,Locale.getDefault()));
    		}
    		return ResponseEntity.ok(result);
    	}catch(Exception e) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    	}

    }

    @PostMapping
    @ApiOperation("Crea un Miembro")
    @ApiResponses({
          @ApiResponse(code = 200, message = "Operación exitosa"),
          @ApiResponse(code = 404, message = "Solicitud incorrecta")
        })
    public ResponseEntity<?> createMember(@Valid @ModelAttribute(name = "memberCreationDto") MemberRequestDto memberRequestDto){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(iMember.createMember(memberRequestDto));
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }



    @PutMapping(path = "/{id}")
    @ApiOperation("Actualiza un miembro por el id")
    @ApiResponses({
          @ApiResponse(code = 200, message = "Operación exitosa"),
          @ApiResponse(code = 404, message = "Solicitud incorrecta")
        })
	public ResponseEntity<?> updateMember(@PathVariable("id") Long id, @Valid @ModelAttribute(name = "memberCreationDto") MemberRequestDto memberRequestDto)
    {
		try {
			return new ResponseEntity<>(iMember.updateMemberById(id, memberRequestDto), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}


	@DeleteMapping(path = "/{id}")
    @ApiOperation("Elimina un miembro por el id")
    @ApiResponses({
          @ApiResponse(code = 200, message = "Operación exitosa"),
          @ApiResponse(code = 404, message = "El miembro con el ID especificado no existe")
        })
	public ResponseEntity<String> deleteMember(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(iMember.deleteMember(id), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

