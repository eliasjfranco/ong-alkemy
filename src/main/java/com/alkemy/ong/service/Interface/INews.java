package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.request.NewsRequestDto;
import com.alkemy.ong.dto.response.CommentResponseDto;
import com.alkemy.ong.dto.response.NewsResponseDto;
import com.alkemy.ong.model.News;
import org.springframework.data.domain.Page;

import java.util.List;

public interface INews {


    News getNewById(Long id);

    List<News> findAll();

    NewsResponseDto save(NewsRequestDto newsRequestDto);

    String deleteNews(Long id);

    NewsResponseDto updateNews(Long id, NewsRequestDto newsRequestDto);

    Page<NewsResponseDto> getAllNewsPaginated(int page, int limit, String sortBy, String sortDir);

    List<CommentResponseDto> getAllCommentsByPost(Long id);
}
