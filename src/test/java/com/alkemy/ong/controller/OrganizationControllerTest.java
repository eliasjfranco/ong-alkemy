package com.alkemy.ong.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import com.alkemy.ong.dto.request.OrganizationRequestDto;
import com.alkemy.ong.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import com.alkemy.ong.dto.SocialNetworkDto;
import com.alkemy.ong.dto.response.OrganizationResponseDto;
import com.alkemy.ong.repository.OrganizationRepository;
import com.alkemy.ong.security.JwtEntryPoint;
import com.alkemy.ong.security.JwtProvider;
import com.alkemy.ong.service.Interface.IOrganization;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.alexaforbusiness.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class OrganizationControllerTest {
	
    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MessageSource message;
	   
	@InjectMocks
	OrganizationController controller;

	@MockBean
	IOrganization iOrganization;
	
	@MockBean
	OrganizationRepository repository;
	
	@Autowired
	ObjectMapper mapper;
	
	@MockBean
	ModelMapper mMapper;
	
	@MockBean
	UserServiceImpl user;
	
	@MockBean
	JwtEntryPoint jwt;
	
	@MockBean
	JwtProvider jwtP;
	
	@MockBean
	ProjectionFactory projectionFactory;
	
	String url = "/organization";
	
	private OrganizationRequestDto dto;
	private OrganizationResponseDto responseDto;
	
    @BeforeEach
    public void init() {
        MockMultipartFile file 
        = new MockMultipartFile(
          "file", 
          "hello.txt", 
          MediaType.TEXT_PLAIN_VALUE, 
          "Hello, World!".getBytes()
        );
    	
        dto = new OrganizationRequestDto();

        dto.setName("David Haye");
        dto.setImage(file);
        dto.setEmail("daxhaye@gmail.com");
        dto.setWelcomeText("Hola Ong");
        
        responseDto = projectionFactory.createProjection(OrganizationResponseDto.class, dto);
        
	}
	
	@Test
	public void testGetOrganization_ok() throws Exception {
		url = url + "/public";
		mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void testGetOrganization_NotFound() throws Exception {
		Long id = 100l;
		url = url + "/" + id;
		
		Mockito.when(iOrganization.getById(id)).thenThrow(NotFoundException.class);

		
		mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void testNewOrganizationCreate_Created() throws Exception {
		
        MockMultipartFile file 
        = new MockMultipartFile(
          "file", 
          "hello.txt", 
          MediaType.TEXT_PLAIN_VALUE, 
          "Hello, World!".getBytes()
        );
		
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("name", "David Haye")
				.param("email", "daxhaye@gmail.com")
				.param("welcomeText", "Hola Ong"))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
	}
	
	@Test
	public void testNewOrganizationCreate_WithoutName() throws Exception {
		
        MockMultipartFile file 
        = new MockMultipartFile(
          "file", 
          "hello.txt", 
          MediaType.TEXT_PLAIN_VALUE, 
          "Hello, World!".getBytes()
        );
		
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("email", "daxhaye@gmail.com")
				.param("welcomeText", "Hola Ong"))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		assertEquals(message.getMessage("organization.error.empty.name", null, Locale.getDefault()), iOrganization.newOrg(dto));
		
	}
	
	@Test
	public void testUpdateOrganiation_OK() throws Exception {
		Mockito.when(iOrganization.newOrg(dto)).thenReturn(responseDto);
		
		MockMultipartFile file 
	        = new MockMultipartFile(
	          "file", 
	          "hello.txt", 
	          MediaType.TEXT_PLAIN_VALUE, 
	          "Hello, World!".getBytes()
	    );
			
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("name", "David Haye")
				.param("email", "daxhaye@gmail.com")
				.param("welcomeText", "Hola Ong"))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		Mockito.when(iOrganization.updateOrg(1l, dto)).thenReturn(responseDto);
		
		MockMultipartHttpServletRequestBuilder multiparti = (MockMultipartHttpServletRequestBuilder)
		MockMvcRequestBuilders.multipart(url + "/1").with(request -> {
			request.setMethod(HttpMethod.PUT.toString());
	        return request;
	    });
		
		mockMvc.perform(multiparti
				.file("image", file.getBytes())
				.param("name", "Juan carlos")
				.param("email", "daxhaye@gmail.com")
				.param("welcomeText", "Hola Ong"))
		.andExpect(MockMvcResultMatchers.status().isOk());		
		
	}
	
	@Test
	public void testUpdateOrganiation_ERROR() throws Exception {
		Mockito.when(iOrganization.newOrg(dto)).thenReturn(responseDto);
		
		MockMultipartFile file 
	        = new MockMultipartFile(
	          "file", 
	          "hello.txt", 
	          MediaType.TEXT_PLAIN_VALUE, 
	          "Hello, World!".getBytes()
	    );
			
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("name", "David Haye")
				.param("email", "daxhaye@gmail.com")
				.param("welcomeText", "Hola Ong"))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		Mockito.when(iOrganization.updateOrg(1l, dto)).thenReturn(responseDto);
		
		MockMultipartHttpServletRequestBuilder multiparti = (MockMultipartHttpServletRequestBuilder)
		MockMvcRequestBuilders.multipart(url + "/1").with(request -> {
			request.setMethod(HttpMethod.PUT.toString());
	        return request;
	    });
		
		mockMvc.perform(multiparti
				.file("image", file.getBytes())
				.param("email", "daxhaye@gmail.com")
				.param("welcomeText", "Hola Ong"))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		assertEquals(message.getMessage("organization.error.empty.name", null, Locale.getDefault()), iOrganization.newOrg(dto));
		
		
	}
	
	@Test
	public void testDeleteOrg() {

		OrganizationRequestDto dto = new OrganizationRequestDto();
	
		dto.setName("David Haye");
        dto.setImage(new MockMultipartFile(
  	          "file", 
  	          "hello.txt", 
  	          MediaType.TEXT_PLAIN_VALUE, 
  	          "Hello, World!".getBytes()
  	    ));
        dto.setEmail("daxhaye@gmail.com");
        dto.setWelcomeText("Hola Ong");
        
        iOrganization.newOrg(dto);
        
        assertEquals(message.getMessage("organization.delete.successful", null, Locale.getDefault()), iOrganization.deleteOrganization(1l));

        assertEquals(message.getMessage("organization.error.notFound", null, Locale.getDefault()), iOrganization.deleteOrganization(1l));
		
	}
	
	@Test
	public void testNewContactOrganization_OK() throws Exception {
		Long id = 1l;
		
		url = url + "/newContact" + "/" + id;

		OrganizationRequestDto dto = new OrganizationRequestDto();
		
		dto.setName("David Haye");
        dto.setImage(new MockMultipartFile(
  	          "file", 
  	          "hello.txt", 
  	          MediaType.TEXT_PLAIN_VALUE, 
  	          "Hello, World!".getBytes()
  	    ));
        dto.setEmail("daxhaye@gmail.com");
        dto.setWelcomeText("Hola Ong");
        
        iOrganization.newOrg(dto);
		
		SocialNetworkDto sn = new SocialNetworkDto();
		sn.setLink("linkedin/com");
		sn.setName("Linkedin");
		String json = mapper.writeValueAsString(sn);
		
		Mockito.when(iOrganization.newContact(1l, sn)).thenReturn(sn);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		
	}

}