package com.alkemy.ong.controller;

import com.alkemy.ong.dto.response.CommentResponseDto;
import com.alkemy.ong.service.Interface.IComment;

import java.util.Locale;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.alkemy.ong.dto.request.CommentRequestDto;
import com.alkemy.ong.exception.CommentNotFoundException;
import com.alkemy.ong.exception.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comments")
@Api(value = "Comments controller")
public class CommentController {

	private final MessageSource message;
	private final IComment iComment;

	@Autowired
	public CommentController(MessageSource message, IComment iComment) {
		this.message = message;
		this.iComment = iComment;
	}


	@GetMapping
	public ResponseEntity<List<CommentResponseDto>> commentsOrderedByDate(){
		return ResponseEntity.status(HttpStatus.OK).body(iComment.commentsOrderedByDate());
	}

	@PostMapping
	@ApiResponses({
			@ApiResponse(code = 201, message = "Comentario creado satisfactoriamente."),
			@ApiResponse(code = 400, message = "No se pudo crear el comentario.")
	})
	public ResponseEntity<?> addComment(@RequestBody @Valid CommentRequestDto dto){
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return ResponseEntity.status(HttpStatus.CREATED).body(iComment.createComment(authentication.getName(),dto));
		} catch(Exception e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message.getMessage("comment.error.create", null, Locale.getDefault()));
		}
	}

	@DeleteMapping(path = "/{id}")
	@ApiResponses({
			@ApiResponse(code = 200, message = "Comentario eliminado satisfactoriamente."),
			@ApiResponse(code = 404, message = "No existe comentario con el ID especificado."),
			@ApiResponse(code = 409, message = "No posee los permisos para realizar esta operación.")
	})
	public ResponseEntity<?> deleteComment(@PathVariable("id") Long id, HttpServletRequest request){
		try{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return ResponseEntity.status(HttpStatus.OK).body(iComment.deleteComment(id, authentication));
		} catch (EntityNotFoundException e){
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (UnsupportedOperationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}

	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto comment) throws CommentNotFoundException, InvalidUserException {
		try {
				CommentResponseDto updatedComment = iComment.updateComment(id, comment);
				return new ResponseEntity<>(updatedComment, HttpStatus.OK);
		} catch (CommentNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

}

