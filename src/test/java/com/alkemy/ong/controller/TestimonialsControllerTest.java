package com.alkemy.ong.controller;

import com.alkemy.ong.dto.request.TestimonialsCreationDto;
import com.alkemy.ong.dto.response.TestimonialsResponseDto;
import com.alkemy.ong.model.Testimonials;
import com.alkemy.ong.service.impl.TestimonialsServiceImpl;
import com.alkemy.ong.service.impl.UsersServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class TestimonialsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestimonialsServiceImpl testimonialsService;

    @InjectMocks
    private TestimonialsController testimonialsController;

    @MockBean
    private ProjectionFactory projectionFactory;

    @MockBean
    private UsersServiceImpl usersService;

    ObjectMapper objectMapper;
    ModelMapper modelMapper;

    private final Testimonials testimonials = new Testimonials("Nombre del testimonio", "Contenido del testimonio");
    private final String endpoint = "/testimonials";

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        modelMapper = new ModelMapper();
        testimonials.setId(123L);
    }

    @Test
    @DisplayName("POST /testimonials")
    void createTestimonials() throws Exception {
        //Given
        TestimonialsCreationDto testimonialsCreationDto = modelMapper.map(testimonials, TestimonialsCreationDto.class);
        Mockito.when(testimonialsService.createTestimonials(any(TestimonialsCreationDto.class))).thenReturn(any(TestimonialsResponseDto.class));
        //When
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("testimonialsCreationDto", testimonialsCreationDto)
                .content(objectMapper.writeValueAsString(testimonialsCreationDto))
                .characterEncoding("UTF-8"))
        //Then
                .andExpect(status().isCreated());

        verify(testimonialsService).createTestimonials(isA(TestimonialsCreationDto.class));
    }

    @Test
    @DisplayName("PUT /testimonials/{id}")
    void update() throws Exception {
        TestimonialsCreationDto testimonialsCreationDto = modelMapper.map(testimonials, TestimonialsCreationDto.class);
        TestimonialsResponseDto testimonialsResponseDto = projectionFactory.createProjection(TestimonialsResponseDto.class, testimonialsCreationDto);
        doReturn(testimonials).when(testimonialsService).getTestimonialsById(testimonials.getId());
        doReturn(testimonialsResponseDto).when(testimonialsService).updateTestimonials(testimonials.getId(), testimonialsCreationDto);

        mockMvc.perform(put(endpoint + "/{id}", testimonials.getId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("testimonialsCreationDto", testimonialsCreationDto)
                .content(objectMapper.writeValueAsString(testimonialsCreationDto))
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testimonialsService).updateTestimonials(anyLong(), any(TestimonialsCreationDto.class));
    }

    @Test
    @DisplayName("DELETE /testimonials/{id}")
    void deleteTestimonialById() throws Exception {
        mockMvc.perform(delete(endpoint + "/{id}", testimonials.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testimonialsService).deleteById(testimonials.getId());
    }

    @Test
    @DisplayName("GET /testimonials")
    void allPagination() throws Exception {
        List<Testimonials> testimonials = new ArrayList<>();
        Page<Testimonials> pagedTestimonials = new PageImpl(testimonials);
        when(testimonialsService.showAllTestimonials(any(Pageable.class))).thenReturn(pagedTestimonials);

        mockMvc.perform(get(endpoint)).andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(testimonialsService).showAllTestimonials(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
    }

}