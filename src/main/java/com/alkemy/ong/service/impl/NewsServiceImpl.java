package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.request.NewsRequestDto;
import com.alkemy.ong.dto.response.CommentResponseDto;
import com.alkemy.ong.dto.response.NewsResponseDto;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.model.News;
import com.alkemy.ong.repository.CommentRepository;
import com.alkemy.ong.repository.NewsRepository;
import com.alkemy.ong.service.Interface.ICategory;
import com.alkemy.ong.service.Interface.IFileStore;
import com.alkemy.ong.service.Interface.INews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;
import java.util.Date;


import org.springframework.context.MessageSource;

import javax.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Locale;


@Service
public class NewsServiceImpl implements INews {

    private final NewsRepository newsRepository;
    private final ProjectionFactory projectionFactory;
    private final MessageSource messageSource;
    private final IFileStore fileStore;
    private final ICategory categoriesService;
    private final CommentRepository commentRepository;

    private static final String ASC = "asc";

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, ProjectionFactory projectionFactory, MessageSource messageSource, IFileStore fileStore, ICategory categoriesService, CommentRepository commentRepository) {
        this.newsRepository = newsRepository;
        this.projectionFactory = projectionFactory;
        this.messageSource = messageSource;
        this.fileStore = fileStore;
        this.categoriesService = categoriesService;
        this.commentRepository = commentRepository;
    }


    public News getNewById(Long id) {
        return newsRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        messageSource.getMessage("news.error.not.found", null, Locale.getDefault())
                )
        );
    }

    @Override
    public List<News> findAll() {
        return null;
    }

    @Override
    public NewsResponseDto save(NewsRequestDto newsRequestDto) {

        Category categoryEntity = categoriesService.findCategoriesById(newsRequestDto.getCategory());

        News newsEntity = News.builder()
                .name(newsRequestDto.getName())
                .content(newsRequestDto.getContent())
                .category(categoryEntity)
                .build();
        News newsCreated = newsRepository.save(newsEntity);
        

        if(newsRequestDto.getImage() != null)
        	newsCreated.setImage(fileStore.save(newsEntity, newsRequestDto.getImage()));

        
        return projectionFactory.createProjection(NewsResponseDto.class, newsRepository.save(newsCreated));
    }

    @Override
    public String deleteNews(Long id) {
        News newsEntity = getNewById(id);
        newsRepository.delete(newsEntity);
        fileStore.deleteFilesFromS3Bucket(newsEntity);

        return messageSource.getMessage(
                "new.delete.successful", null, Locale.getDefault()
        );
    }

    @Override
    public NewsResponseDto updateNews(Long id, NewsRequestDto newsRequestDto) {

        News newsUpdated = getNewById(id);
        Category categoryEntity = categoriesService.findCategoriesById(newsRequestDto.getCategory());

        newsUpdated.setCategory(categoryEntity);

        newsUpdated.setContent(newsRequestDto.getContent());
        newsUpdated.setName(newsRequestDto.getName());
        if(newsRequestDto.getImage() != null)
            newsUpdated.setImage(fileStore.save(newsUpdated, newsRequestDto.getImage()));

        newsUpdated.setEdited(new Date());
        return projectionFactory.createProjection(NewsResponseDto.class, newsRepository.save(newsUpdated));
    }

    @Override
    public Page<NewsResponseDto> getAllNewsPaginated(int page, int limit, String sortBy, String sortDir) {
        if (page > 0) {
            page = page - 1;
        }

        Pageable pageable = PageRequest.of(
                page, limit,
                sortDir.equalsIgnoreCase(ASC) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );

        return newsRepository.findAllProjectedBy(pageable);
    }

    @Override
    public List<CommentResponseDto> getAllCommentsByPost(Long id) {
        News news = getNewById(id);

        if(id == null) throw new EntityNotFoundException(
                messageSource.getMessage("news.error.object.notFound", null, Locale.getDefault())
        );

        return commentRepository.findByNewsOrderByCreatedDesc(news);
    }


}
