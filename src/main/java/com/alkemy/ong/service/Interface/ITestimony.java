package com.alkemy.ong.service.Interface;


import com.alkemy.ong.dto.request.TestimonyRequestDto;
import com.alkemy.ong.dto.response.TestimonialsResponseDto;
import com.alkemy.ong.model.Testimony;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITestimony {

    Testimony getTestimonialsById(Long id);

    String deleteById(Long id);

    TestimonialsResponseDto createTestimonials(TestimonyRequestDto testimonyRequestDto);

    TestimonialsResponseDto updateTestimonials(Long id, TestimonyRequestDto testimonyRequestDto);

    Page<Testimony> showAllTestimonials(Pageable pageable);
}
