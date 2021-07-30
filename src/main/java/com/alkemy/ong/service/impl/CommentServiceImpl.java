package com.alkemy.ong.service.impl;


import com.alkemy.ong.Enum.ERole;
import com.alkemy.ong.dto.response.CommentResponseDto;
import com.alkemy.ong.exception.CommentNotFoundException;
import com.alkemy.ong.model.Comment;
import com.alkemy.ong.repository.CommentRepository;
import com.alkemy.ong.service.Interface.IComment;
import com.alkemy.ong.service.Interface.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityNotFoundException;


import com.alkemy.ong.service.Interface.INews;
import org.springframework.data.projection.ProjectionFactory;

import com.alkemy.ong.dto.request.CommentRequestDto;

import com.alkemy.ong.model.News;
import com.alkemy.ong.model.User;




@Service
public class CommentServiceImpl implements IComment {

	private final IUser usersService;
	private final INews newsService;
	private final ProjectionFactory projectionFactory;
	private final CommentRepository repoComment;
	private final MessageSource messageSource;

	@Autowired
	public CommentServiceImpl(IUser usersService, INews newsService, ProjectionFactory projectionFactory, CommentRepository repoComment, MessageSource messageSource) {
		this.usersService = usersService;
		this.newsService = newsService;
		this.projectionFactory = projectionFactory;
		this.repoComment = repoComment;
		this.messageSource = messageSource;
	}


	public List<CommentResponseDto> commentsOrderedByDate() {return (List<CommentResponseDto>) repoComment.findAllByOrderCreatedDesc();}

	@Override
	public CommentResponseDto createComment(String email, CommentRequestDto dto) {
		
		User user = usersService.getUser(email);
		News post = newsService.getNewById(dto.getNews());
		Comment comment = new Comment(
				user,
				dto.getBody(),
				post
		);
		System.out.println(user.getId());
		return projectionFactory.createProjection(CommentResponseDto.class, repoComment.save(comment));
	}

	@Override
	public CommentResponseDto updateComment(Long id, CommentRequestDto comment) throws CommentNotFoundException {
		Comment updatedComment = getCommentById(id);
		updatedComment.setBody(comment.getBody());
		updatedComment.setEdited(new Date());
		return projectionFactory.createProjection(CommentResponseDto.class, repoComment.save(updatedComment));
	}


	@Override
	public String deleteComment(Long id, Authentication authentication) {
		Comment comment = getCommentById(id);
		if(authentication.getAuthorities().stream().noneMatch(r -> r.getAuthority().equals(ERole.ROLE_ADMIN.toString())) && !comment.getUser().getEmail().equals(authentication.getName()))
			throw new UnsupportedOperationException(messageSource.getMessage("comment.error.invalid.user",null,Locale.getDefault()));

		repoComment.delete(comment);
		return messageSource.getMessage("comment.delete.successful",null, Locale.getDefault());
	}

	public Comment getCommentById(Long id){
		return repoComment.findById(id).orElseThrow(
				() -> new EntityNotFoundException(
						messageSource.getMessage("comment.error.notFound",null,Locale.getDefault())
				)
		);
	}

}
