package com.alkemy.ong.service.Interface;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alkemy.ong.dto.request.CategoryRequestDto;
import com.alkemy.ong.dto.response.CategoryResponseDto;
import com.alkemy.ong.model.Category;


public interface ICategory {

	CategoryResponseDto findById(Long id);

	List<CategoryResponseDto> findAll();

	CategoryResponseDto createCategory(CategoryRequestDto category);

	String deleteById(Long id);

	Category findCategoriesById(Long id);
	
	Page<CategoryResponseDto> findAllWithNameInPage(Pageable pageable);

	CategoryResponseDto updateCategoryById(Long id, CategoryRequestDto dto);

}
