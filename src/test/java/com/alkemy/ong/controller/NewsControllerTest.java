package com.alkemy.ong.controller;

import com.alkemy.ong.dto.request.NewsCreationDto;
import com.alkemy.ong.dto.response.CommentResponseDto;
import com.alkemy.ong.dto.response.NewsResponseDto;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.model.News;
import com.alkemy.ong.service.impl.CategoriesServiceImpl;
import com.alkemy.ong.service.impl.NewsServiceImpl;
import com.alkemy.ong.service.impl.UsersServiceImpl;
import com.alkemy.ong.util.UsersSeeder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersSeeder usersSeeder;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UsersServiceImpl usersService;

    private final String BASE_URL = "/news";

    @InjectMocks
    private NewsController newsController;

    @MockBean
    private NewsServiceImpl newsService;

    @MockBean
    private CategoriesServiceImpl categoriesService;

    @MockBean
    private ProjectionFactory projectionFactory;

    private Category category = new Category("nombre", "description");

    private News news = new News("Nombre", "content", category);


    ModelMapper modelMapper;
    ObjectMapper objectMapper;

    CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).alwaysDo(MockMvcResultHandlers.print()).build();
        Category category = categoriesService.findCategoriesById(1L);

        news.setCategory(category);
        news.setId(2L);
    }

    @Test
    void getNews() throws Exception{
        Long id = 1L;
        String name = "nueva plaza";
        String url = BASE_URL + "/" +id;
        /*when(newsService.getNewById(anyLong())).thenReturn(news);*/
        given(newsService.getNewById(anyLong())).willReturn(news);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void createNews() throws Exception{
        NewsCreationDto newsCreationDto = modelMapper.map(news, NewsCreationDto.class);
        MockMultipartFile file = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());
        newsCreationDto.setCategory(2l);
        when(newsService.save(any(NewsCreationDto.class))).thenReturn(any(NewsResponseDto.class));

        MockMultipartHttpServletRequestBuilder mockMultipartHttpServletRequestBuilder = (MockMultipartHttpServletRequestBuilder)
                MockMvcRequestBuilders.multipart(BASE_URL, news.getId()).with(request -> {
                    request.setMethod(HttpMethod.POST.toString());
                    return request;
                });

        mockMvc.perform(MockMvcRequestBuilders
                .multipart(BASE_URL)
                .file("image", file.getBytes())
                .param("name", "parque")
                .param("content", "parque etc")
                .param("category", String.valueOf(1l)))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

            verify(newsService).save(isA(NewsCreationDto.class));
    }

    @Test
    void deleteNews() throws Exception{
        Long id = 1l;
        Mockito.doNothing().when(newsService).deleteNews(id);
        String url = BASE_URL + "/delete/" + id;
        mockMvc.perform(MockMvcRequestBuilders.delete(url)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateNews() throws Exception{

        MockMultipartFile file = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

        String url = BASE_URL + "/{id}";

        NewsCreationDto updateDto = modelMapper.map(news, NewsCreationDto.class);
        updateDto.setName(news.getName());
        updateDto.setContent(news.getContent());
        updateDto.setCategory(2l);
        NewsResponseDto newsResponseDto = projectionFactory.createProjection(NewsResponseDto.class, updateDto);
        when(newsService.updateNews(1l, updateDto)).thenReturn(newsResponseDto);

        MockMultipartHttpServletRequestBuilder mockMultipartHttpServletRequestBuilder = (MockMultipartHttpServletRequestBuilder)
                MockMvcRequestBuilders.multipart(url, news.getId()).with(request -> {
                    request.setMethod(HttpMethod.PUT.toString());
                    return request;
                });

                mockMvc.perform(mockMultipartHttpServletRequestBuilder
                    .file("image", file.getBytes())
                    .param("name", "parque")
                    .param("content", "parque etc")
                    .param("category", String.valueOf(1l)))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getNewsPaginated() throws Exception {
        List<News> newss = new ArrayList<>();
        Page<NewsResponseDto> pagedNewss = new PageImpl(newss);
        when(newsService.getAllNewsPaginated(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pagedNewss);

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(newsService).getAllNewsPaginated(intCaptor.capture(), intCaptor.capture(), stringCaptor.capture(), stringCaptor.capture());
        int page = intCaptor.getValue();
        int limit = intCaptor.getValue();
        String sortBy = stringCaptor.getValue();
        String sortDir = stringCaptor.getValue();

        Pageable pageable = PageRequest.of(
                page, limit,
                sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );

        assertThat(pageable.getPageNumber()).isEqualTo(10);
        assertThat(pageable.getPageSize()).isEqualTo(10);

    }

    @Test
    void getCommentsByPost() throws Exception {
        Long id = 1l;
        String url = BASE_URL + "/" +id + "/comments";
        List<CommentResponseDto>comments = new ArrayList<>();
        comments.add(commentResponseDto);
        Mockito.when(newsService.getAllCommentsByPost(id)).thenReturn(comments);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MimeTypeUtils.APPLICATION_JSON_VALUE));

        resultActions.andExpect(status().isOk());
    }
}