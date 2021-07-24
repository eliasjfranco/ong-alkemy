package com.alkemy.ong.controller;


import com.alkemy.ong.model.User;
import com.alkemy.ong.security.JwtEntryPoint;
import com.alkemy.ong.security.JwtProvider;
import com.alkemy.ong.service.Interface.IUsersService;
import com.alkemy.ong.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    IUsersService iUsersService;

    @InjectMocks
    UserController userController;

    @MockBean
    UsersServiceImpl usersService;

    @MockBean
    JwtEntryPoint jwtEntryPoint;

    @MockBean
    JwtProvider jwtProvider;


     String URL = "/users";
     User user;



    @Test
    @DisplayName("FindAll Users")
    void showAllUsers_403() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(URL)).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("Delete user by id")
    void deleteUser_Not_found() {

        assertEquals(HttpStatus.NOT_FOUND,userController.deleteUser(1L).getStatusCode());
    }

    @Test
    @DisplayName("Delete user by id")
    void deleteUser_Ok() throws Exception {
        Long id = 1l;
        URL = URL + "/" + id;
        user = new User();

        user.setFirstName("rogelio");
        user.setLastName("querido");
        user.setEmail("rogelio@gmail.com");
        user.setPassword("1234567");

        Mockito.when(usersService.getUserById(id)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.delete(URL)).andExpect(MockMvcResultMatchers.status().isOk());
    }
}