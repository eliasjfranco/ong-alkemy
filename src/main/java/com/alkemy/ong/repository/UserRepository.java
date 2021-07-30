package com.alkemy.ong.repository;

import java.util.List;
import java.util.Optional;

import com.alkemy.ong.dto.response.UserResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.model.User;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findById(Long id);

	Optional<User> findByEmail(String email);

	List<UserResponseDto> findAllProjectedBy();

	boolean existsByEmail(String email);

	Optional<User> findByFirstName(String firstName);
	// DE AQUÍ PARA ABAJO, BORRAR PARA ETAPA DE PRODUCCIÓN
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO roles (id, deleted, name, role_name) VALUES (1, false, 'ROLE_USER', 'ROLE_USER');", nativeQuery = true)
	void createUserRole();

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO roles (id, deleted, name, role_name) VALUES (2, false, 'ROLE_ADMIN', 'ROLE_ADMIN');", nativeQuery = true)
	void createAdminRole();

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO user_role (user_id, role_id) values (?1, 2);", nativeQuery = true)
	void setAdminRole(Long i);
}
