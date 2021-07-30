package com.alkemy.ong.service.Interface;


import java.util.List;


import com.alkemy.ong.dto.request.CommentRequestDto;
import com.alkemy.ong.dto.response.CommentResponseDto;
import com.alkemy.ong.exception.CommentNotFoundException;
import com.alkemy.ong.model.Comment;
import org.springframework.security.core.Authentication;

public interface IComment {
    List<CommentResponseDto> commentsOrderedByDate();

    CommentResponseDto createComment(String email, CommentRequestDto dto);

    String deleteComment(Long id, Authentication authentication);

    Comment getCommentById(Long id);

    CommentResponseDto updateComment(Long id, CommentRequestDto comment) throws CommentNotFoundException;
}

