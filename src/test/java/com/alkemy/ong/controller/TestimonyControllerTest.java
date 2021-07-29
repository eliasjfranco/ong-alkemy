package com.alkemy.ong.controller;

import com.alkemy.ong.dto.request.TestimonyRequestDto;
import com.alkemy.ong.dto.response.TestimonialsResponseDto;
import com.alkemy.ong.model.Testimony;
import com.alkemy.ong.service.impl.TestimonyServiceImpl;
import com.alkemy.ong.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class TestimonyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestimonyServiceImpl testimonialsService;

    @InjectMocks
    private TestimonyController testimonyController;

    @MockBean
    private ProjectionFactory projectionFactory;

    @MockBean
    private UserServiceImpl usersService;

    @Autowired
    private MessageSource messageSource;

    ObjectMapper objectMapper;
    ModelMapper modelMapper;

    private final Testimony testimony = new Testimony("Nombre del testimonio", "Contenido del testimonio");
    private final String endpoint = "/testimonials";

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        modelMapper = new ModelMapper();
        testimony.setId(123L);
    }

    @Test
    @DisplayName("POST /testimonials")
    void createTestimonials() throws Exception {
        //Given
        TestimonyRequestDto testimonyRequestDto = modelMapper.map(testimony, TestimonyRequestDto.class);
        Mockito.when(testimonialsService.createTestimonials(any(TestimonyRequestDto.class))).thenReturn(any(TestimonialsResponseDto.class));
        //When
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("testimonialsCreationDto", testimonyRequestDto)
                .content(objectMapper.writeValueAsString(testimonyRequestDto))
                .characterEncoding("UTF-8"))
        //Then
                .andExpect(status().isCreated());

        verify(testimonialsService).createTestimonials(isA(TestimonyRequestDto.class));
    }

    @Test
    void createTestimonialsError() throws Exception {
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
                //Then
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("PUT /testimonials/{id}")
    void update() throws Exception {
        TestimonyRequestDto testimonyRequestDto = modelMapper.map(testimony, TestimonyRequestDto.class);
        TestimonialsResponseDto testimonialsResponseDto = projectionFactory.createProjection(TestimonialsResponseDto.class, testimonyRequestDto);
        doReturn(testimony).when(testimonialsService).getTestimonialsById(testimony.getId());
        doReturn(testimonialsResponseDto).when(testimonialsService).updateTestimonials(testimony.getId(), testimonyRequestDto);

        mockMvc.perform(put(endpoint + "/{id}", testimony.getId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("testimonialsCreationDto", testimonyRequestDto)
                .content(objectMapper.writeValueAsString(testimonyRequestDto))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testimonialsService).updateTestimonials(anyLong(), any(TestimonyRequestDto.class));
    }

    @Test
    @DisplayName("PUT /testimonials/{not found ID}")
    void updateTestimonyError() throws Exception {
        Long id = 95959959L;
        mockMvc.perform(put(endpoint + "/{id}", id)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        assertThat(id).isNotEqualTo(testimony.getId());
    }

    @Test
    @DisplayName("DELETE /testimonials/{id}")
    void deleteTestimonialById() throws Exception {
        mockMvc.perform(delete(endpoint + "/{id}", testimony.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testimonialsService).deleteById(testimony.getId());
    }

    @Test
    @DisplayName("DELETE /testimonials/{not found ID}")
    void deleteTestimonyError() throws Exception {
        Assertions.assertEquals(HttpStatus.NOT_FOUND,testimonyController.deleteTestimonialById(1L).getStatusCode());
    }

    @Test
    @DisplayName("GET /testimonials")
    void allPagination() throws Exception {
        List<Testimony> testimonials = new ArrayList<>();
        Page<Testimony> pagedTestimonials = new PageImpl(testimonials);
        when(testimonialsService.showAllTestimonials(any(Pageable.class))).thenReturn(pagedTestimonials);

        mockMvc.perform(get(endpoint)).andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(testimonialsService).showAllTestimonials(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
    }

}