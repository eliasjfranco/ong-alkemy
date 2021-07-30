package com.alkemy.ong.controller;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.alkemy.ong.dto.request.MemberRequestDto;
import com.alkemy.ong.dto.response.MemberResponseDto;
import com.alkemy.ong.model.Member;
import com.alkemy.ong.security.JwtEntryPoint;
import com.alkemy.ong.security.JwtProvider;
import com.alkemy.ong.service.Interface.IMember;
import com.alkemy.ong.service.impl.UserServiceImpl;
import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc
@WithMockUser
public class MemberControllerTest {

	@MockBean
	private ProjectionFactory mapper;

	@MockBean
	private JwtEntryPoint jwtEntry;
	
	@MockBean
	private JwtProvider jwtProvider;
	
	@MockBean
	private ObjectMapper objectMapper;
	
	@Autowired
    private MockMvc mockMvc;

	@MockBean
	private UserServiceImpl userService;

	@InjectMocks
	private MemberController controller;
	
	@MockBean
	private MessageSource message;
	
	@MockBean
	private IMember iMember;
		
	@MockBean
	private Pageable pageable;
	
	@MockBean
	private Page<?> page;
	
	private MemberResponseDto responseDto;

	private MemberRequestDto dto;
	String url = "/members";
	
	@BeforeEach
	public void init() {
		
		MockMultipartFile file = new MockMultipartFile
				("name", "image.txt", MediaType.TEXT_PLAIN_VALUE, "image".getBytes());
				
		
		dto = new MemberRequestDto();
		dto.setName("Raul");
		dto.setImage(file);
		dto.setDescription("The Raul description");
		dto.setFacebookUrl("faceboot.com/raulito");
		dto.setInstagramUrl("instagaram.com/raulitoski");
		dto.setLinkedinUrl("linkedIn.com/raulDev");
		
		responseDto = mapper.createProjection(MemberResponseDto.class, dto);
		
	}
	

	
	@Test
	public void createMemberSuccessTest() throws Exception  {
	
		
		Mockito.when(iMember.createMember(dto)).thenReturn(responseDto);
		
		
		MockMultipartFile file = new MockMultipartFile
				("name", "image.txt", MediaType.TEXT_PLAIN_VALUE, "image".getBytes());
		
		
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("name", "Raul")
				.param("description", "The Raul description")
				.param("facebookUrl", "faceboot.com/raulito")
				.param("linkedinUrl", "instagaram.com/raulitoski")
				.param("instagramUrl", "linkedIn.com/raulDev"))
			.andExpect(MockMvcResultMatchers.status().isCreated());

	}
	
	
	@Test
	public void createMemberWithoutNameTest() throws Exception  {
		
		Mockito.when(iMember.createMember(dto)).thenReturn(responseDto);
		
		
		MockMultipartFile file = new MockMultipartFile
				("name", "image.txt", MediaType.TEXT_PLAIN_VALUE, "image".getBytes());
		
		
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("description", "The Raul description")
				.param("facebookUrl", "faceboot.com/raulito")
				.param("linkedinUrl", "instagaram.com/raulitoski")
				.param("instagramUrl", "linkedIn.com/raulDev"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
			
		Assertions.assertEquals(message.getMessage("member.error.empty.name", null, Locale.getDefault()), iMember.createMember(dto));


	}
		
	
	@Test
	public void updateMemberSuccessTest() throws IOException, Exception {
		
		Mockito.when(iMember.createMember(dto)).thenReturn(responseDto);
		
		
		MockMultipartFile file = new MockMultipartFile
				("name", "image.txt", MediaType.TEXT_PLAIN_VALUE, "image".getBytes());
		
		
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("name", "Raul")
				.param("description", "The Raul description")
				.param("facebookUrl", "faceboot.com/raulito")
				.param("linkedinUrl", "instagaram.com/raulitoski")
				.param("instagramUrl", "linkedIn.com/raulDev"))
			.andExpect(MockMvcResultMatchers.status().isCreated());
		
		
		Mockito.when(iMember.updateMemberById(1l, dto)).thenReturn(responseDto);
		
	      MockMultipartHttpServletRequestBuilder multiparti = (MockMultipartHttpServletRequestBuilder) 
	      MockMvcRequestBuilders.multipart(url + "/1").with(request -> {
	        request.setMethod(HttpMethod.PUT.toString());
	        return request;
	      });

		mockMvc.perform(multiparti
				.file("image", file.getBytes())
				.param("name", "Ramon")
				.param("description", "The Raul description")
				.param("facebookUrl", "faceboot.com/raulito")
				.param("linkedinUrl", "instagaram.com/raulitoski")
				.param("instagramUrl", "linkedIn.com/raulDev"))
			.andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	
	@Test
	public void updateMemberErrorTest() throws IOException, Exception {
		
		Mockito.when(iMember.createMember(dto)).thenReturn(responseDto);
		
		
		MockMultipartFile file = new MockMultipartFile
				("name", "image.txt", MediaType.TEXT_PLAIN_VALUE, "image".getBytes());
		
		
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file("image", file.getBytes())
				.param("name", "Raul")
				.param("description", "The Raul description")
				.param("facebookUrl", "faceboot.com/raulito")
				.param("linkedinUrl", "instagaram.com/raulitoski")
				.param("instagramUrl", "linkedIn.com/raulDev"))
			.andExpect(MockMvcResultMatchers.status().isCreated());
		
		
		Mockito.when(iMember.updateMemberById(2l, dto)).thenReturn(responseDto);
		
	      MockMultipartHttpServletRequestBuilder multiparti = (MockMultipartHttpServletRequestBuilder) 
	      MockMvcRequestBuilders.multipart(url + "/2").with(request -> {
	        request.setMethod(HttpMethod.PUT.toString());
	        return request;
	      });

		mockMvc.perform(multiparti
				.file("image", file.getBytes())
				.param("description", "The Raul description")
				.param("facebookUrl", "faceboot.com/raulito")
				.param("linkedinUrl", "instagaram.com/raulitoski")
				.param("instagramUrl", "linkedIn.com/raulDev"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}

	
	@Test
	public void getAllMembersSuccessTest() throws Exception {		


		Page<Member> result = iMember.showAllMembers(pageable);

		Mockito.when(iMember.showAllMembers(pageable)).thenReturn(result);

		controller.createMember(dto);
				
		Assertions.assertEquals(result, iMember.showAllMembers(pageable));

	}
	
	
	@Test
	public void deleteMemberSuccessTest() {
			
		MemberRequestDto dto = new MemberRequestDto();
		dto.setName("Raul");
		dto.setImage(null);
		dto.setDescription("The Raul description");
		dto.setFacebookUrl("faceboot.com/raulito");
		dto.setInstagramUrl("instagaram.com/raulitoski");
		dto.setLinkedinUrl("linkedIn.com/raulDev");
		iMember.createMember(dto);
		
		Assertions.assertEquals(message.getMessage("member.delete.successful", null, Locale.getDefault()), iMember.deleteMember(1l));
	
		Assertions.assertEquals(message.getMessage("member.error.not.found", null, Locale.getDefault()), iMember.getMemberById(1l));
		
	}
	
	@Test
	public void deleteMemberNotFoundTest() {
		
		MemberRequestDto dto = new MemberRequestDto();
		dto.setName("Raul");
		dto.setImage(null);
		dto.setDescription("The Raul description");
		dto.setFacebookUrl("faceboot.com/raulito");
		dto.setInstagramUrl("instagaram.com/raulitoski");
		dto.setLinkedinUrl("linkedIn.com/raulDev");
		iMember.createMember(dto);
		
		Assertions.assertEquals(message.getMessage("member.error.not.found", null, Locale.getDefault()), iMember.deleteMember(1l));
	
		
	}
	
   
}
