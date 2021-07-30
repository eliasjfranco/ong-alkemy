package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.request.LoginUsersDto;
import com.alkemy.ong.dto.request.UsersRequestDto;
import com.alkemy.ong.dto.response.UserResponseDto;
import com.alkemy.ong.exception.EmailAlreadyRegistered;
import com.alkemy.ong.exception.NotRegisteredException;
import com.alkemy.ong.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.json.JsonPatch;
import java.io.IOException;
import java.util.List;


public interface IUser extends UserDetailsService {

	UserResponseDto createUser(UsersRequestDto user) throws IOException, EmailAlreadyRegistered;

	User getUser(String email);

	UserResponseDto updateUser(Long id, UsersRequestDto user);

	String deleteUser(Long id);

	User getUserById(Long id);

	UserResponseDto patchUpdate(Long id, Authentication authentication, JsonPatch patchDocument);

	UserDetails loadUserByUsername(String email);

	String loginUser(LoginUsersDto user) throws NotRegisteredException;

	List<UserResponseDto> showAllUsers();


}
