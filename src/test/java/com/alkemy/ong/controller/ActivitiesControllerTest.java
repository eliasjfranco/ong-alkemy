package com.alkemy.ong.controller;


import com.alkemy.ong.controller.ActivitiesController;
import com.alkemy.ong.dto.request.ActivityCreationDto;
import com.alkemy.ong.dto.response.ActivityResponseDto;
import com.alkemy.ong.repository.ActivitiesRepository;
import com.alkemy.ong.security.JwtEntryPoint;
import com.alkemy.ong.security.JwtProvider;
import com.alkemy.ong.service.Interface.IActivitiesService;
import com.alkemy.ong.service.impl.UsersServiceImpl;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.alexaforbusiness.model.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import java.awt.print.Pageable;
import java.util.Locale;



@WebMvcTest(ActivitiesController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class ActivitiesControllerTest {

    @MockBean
    JwtEntryPoint jwtEntryPoint;

    @MockBean
    JwtProvider jwtProvider;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private ActivitiesRepository repository=Mockito.mock(ActivitiesRepository.class);

    @MockBean
    IActivitiesService iActivitiesService ;

    @MockBean
    UsersServiceImpl user;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private Pageable pageable;

    @MockBean
    ModelMapper mapper;

    @MockBean
    private ProjectionFactory projectionFactory;

    @Autowired
    private ActivitiesController ControllerTest = new ActivitiesController(iActivitiesService,projectionFactory,messageSource);


    ActivityResponseDto responseDto ;

    ActivityCreationDto dto = new ActivityCreationDto();

    String url = "/activities";



    @BeforeEach
    public void init(){
        MultipartFile file = new MockMultipartFile(
                "file",
                "img.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        dto = new ActivityCreationDto();
        dto.setImage(file);
        dto.setName("Recreacion");
        dto.setContent("Utilizado para...");
        responseDto = projectionFactory.createProjection(ActivityResponseDto.class,dto);


    }

    @Test
    @DisplayName("GET /activities OK")
    public void getActivitiesTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /activities/id Not Found")
    public void getActivitiesNotFoundTest() throws Exception{
        Long id=1001l;
        url=url+"/"+id;
        Mockito.when(iActivitiesService.getActivityById(id)).thenThrow(NotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().isNotFound());    }

    @Test
    @DisplayName("POST /activities OK")
    public void createActivityTest() throws Exception{


    Mockito.when(iActivitiesService.createActivity(dto)).thenReturn(responseDto);

        ResponseEntity<?> respuesta;
        MultipartFile file = new MockMultipartFile(
                "file",
                "img.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());



        mockMvc.perform(MockMvcRequestBuilders
               .multipart(url)
               .file("image",file.getBytes())
               .param("name","Donar")
               .param("content","Se utiliza para..."))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    @DisplayName("POST /activities Content Bad")
    public void createActivityContentNullTest() throws Exception{


        Mockito.when(iActivitiesService.createActivity(dto)).thenReturn(responseDto);


        MultipartFile file = new MockMultipartFile(
                "file",
                "img.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                .multipart(url)
                .file("image",file.getBytes())
                .param("name","Contaduria"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Assertions.assertEquals(messageSource.getMessage("activity.error.blank.content", null, Locale.getDefault()),iActivitiesService.createActivity(dto));


    }

    @Test
    @DisplayName("PUT /activities/id OK")
    public void updateActivityTest() throws Exception{
        Long id = 2l;
        String urlPut = url + "/" + id;
        Mockito.when(iActivitiesService.createActivity(dto)).thenReturn(responseDto);

        MultipartFile file = new MockMultipartFile(
                "file",
                "img.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());


        mockMvc.perform(MockMvcRequestBuilders
                .multipart(url)
                .file("image",file.getBytes())
                .param("name","Caminatas")
                .param("content","Se trata de.."))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.when(iActivitiesService.updateActivity(id,dto)).thenReturn(responseDto);
        MockMultipartHttpServletRequestBuilder multiparti = (MockMultipartHttpServletRequestBuilder)

                MockMvcRequestBuilders.multipart(urlPut).with(request -> {

                    request.setMethod(HttpMethod.PUT.toString());

                    return request;

                });
        mockMvc.perform(multiparti
                .file("image",file.getBytes())
                .param("name","Salidas")
                .param("content","Se trata de.."))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("PUT /activities/id Name Bad")
    public void updateActivityNotIdTest() throws Exception{
        Long id = 2l;
        Long invalidId=7l;
        String urlPut = url + "/" + id;
        Mockito.when(iActivitiesService.createActivity(dto)).thenReturn(responseDto);

        MultipartFile file = new MockMultipartFile(
                "file",
                "img.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());


        mockMvc.perform(MockMvcRequestBuilders
                .multipart(url)
                .file("image",file.getBytes())
                .param("name","Rayuela..")
                .param("content","Se trata de.."))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.when(iActivitiesService.updateActivity(invalidId,dto)).thenReturn(responseDto);
        MockMultipartHttpServletRequestBuilder multiparti = (MockMultipartHttpServletRequestBuilder)

                MockMvcRequestBuilders.multipart(urlPut).with(request -> {

                    request.setMethod(HttpMethod.PUT.toString());

                    return request;

                });
        mockMvc.perform(multiparti
                .file("image",file.getBytes())
                .param("content","Se trata de.."))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test

    @DisplayName("Delete /activity/id Bad")

    void deleteUser_NOT_FOUND() throws Exception {
       Assertions.assertEquals(HttpStatus.NOT_FOUND,ControllerTest.deleteActivityById(1L).getStatusCode());

    }

}
