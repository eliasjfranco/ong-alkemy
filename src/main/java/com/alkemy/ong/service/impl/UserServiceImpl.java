package com.alkemy.ong.service.impl;

import javax.json.JsonPatch;
import javax.persistence.EntityNotFoundException;


import com.alkemy.ong.Enum.ERole;
import com.alkemy.ong.dto.response.UserResponseDto;
import com.alkemy.ong.exception.EmailAlreadyRegistered;
import com.alkemy.ong.model.Role;
import com.alkemy.ong.repository.RolRepository;

import com.alkemy.ong.dto.request.LoginUsersDto;
import com.alkemy.ong.exception.NotRegisteredException;
import com.alkemy.ong.service.Interface.IFileStore;
import com.alkemy.ong.util.PatchHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alkemy.ong.dto.request.UsersRequestDto;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.security.JwtProvider;
import com.alkemy.ong.service.Interface.IUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements IUser {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MessageSource messageSource;
	private final PatchHelper patchHelper;
	private final RolRepository rolRepository;
	private final JwtProvider jwtProvider;
	private final EmailServiceImpl emailService;
	private final ProjectionFactory projectionFactory;
	private final AuthenticationManager authenticationManager;
	private final IFileStore fileStore;

	public static final String BEARER = "Bearer ";

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MessageSource messageSource, PatchHelper patchHelper, RolRepository rolRepository, JwtProvider jwtProvider, EmailServiceImpl emailService, ProjectionFactory projectionFactory, AuthenticationManager authenticationManager, IFileStore fileStore) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.messageSource = messageSource;
		this.patchHelper = patchHelper;
		this.rolRepository = rolRepository;
		this.jwtProvider = jwtProvider;
		this.emailService = emailService;
		this.projectionFactory = projectionFactory;
		this.authenticationManager = authenticationManager;
		this.fileStore = fileStore;
	}


	@Override
	public UserResponseDto createUser(UsersRequestDto user) throws IOException {

		if(userRepository.findByEmail(user.getEmail()).isPresent())
			throw new EmailAlreadyRegistered(messageSource.getMessage("user.error.email.registered", null, Locale.getDefault()));

		User userEntity = User.builder()
				.email(user.getEmail())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.password(passwordEncoder.encode(user.getPassword()))
				.build();

		Set<Role> roles = new HashSet<>();
		roles.add(rolRepository.findByRoleName(ERole.ROLE_USER).get());
		userEntity.setRoles(roles);
		emailService.registerEmail(user.getEmail());

		User userCreated = userRepository.save(userEntity);

		if(user.getPhoto() != null) {
			userCreated.setPhoto(fileStore.save(userCreated, user.getPhoto()));
		}

		
		return projectionFactory.createProjection(UserResponseDto.class, userRepository.save(userCreated));
	}

	@Override
	public String loginUser(LoginUsersDto user) throws NotRegisteredException {
		if(userRepository.findByEmail(user.getEmail()).isEmpty()) throw new NotRegisteredException(
				messageSource.getMessage("login.error.email.not.registered", null, Locale.getDefault())
		);

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getEmail(),
						user.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		return BEARER + jwtProvider.generatedToken(authentication);

	}

	@Override
	public User getUser(String email) {
		User usr = userRepository.findByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException(
						messageSource.getMessage("user.error.email.not.found", null, Locale.getDefault())
				)
		);
		return usr;
	}

	@Override
	public UserResponseDto updateUser(Long id, UsersRequestDto user) {
		User userEntity = getUserById(id);
		userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

		if(user.getPhoto() != null)
			userEntity.setPhoto(fileStore.save(userEntity, user.getPhoto()));

		return projectionFactory.createProjection(UserResponseDto.class, userRepository.save(userEntity));
	}

	@Override
	public String deleteUser(Long id) {
		User userEntity = getUserById(id);
		fileStore.deleteFilesFromS3Bucket(userEntity);
		userRepository.delete(userEntity);
		return messageSource.getMessage("user.delete.successful", null, Locale.getDefault());
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(
				() -> new EntityNotFoundException(messageSource.getMessage("user.error.not.found", null, Locale.getDefault()))
		);
	}

	//https://cassiomolin.com/2019/06/10/using-http-patch-in-spring/
	@Override
	public UserResponseDto patchUpdate(Long id, Authentication authentication, JsonPatch patchDocument) {
		User user = getUserById(id);
		if(!user.getEmail().equals(authentication.getName()) && authentication.getAuthorities().stream().noneMatch(r -> r.getAuthority().equals(ERole.ROLE_ADMIN.toString())))
			throw new UnsupportedOperationException(messageSource.getMessage("user.error.invalid.permissions", null, Locale.getDefault()));

		User userPatched = patchHelper.patch(patchDocument, user, User.class);
		userPatched.setEdited(new Date());
		return projectionFactory.createProjection(UserResponseDto.class, userRepository.save(userPatched));
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(
						messageSource.getMessage("user.error.email.not.found", null, Locale.getDefault())
				));
		return User.build(user);
	}

	@Override
	public List<UserResponseDto> showAllUsers() {
		return userRepository.findAllProjectedBy();
	}


}