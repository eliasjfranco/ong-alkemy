package com.alkemy.ong.repository;

import com.alkemy.ong.model.Testimony;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestimonyRepository extends JpaRepository<Testimony, Long> {

    Optional<Testimony> findById(Long id);

}
