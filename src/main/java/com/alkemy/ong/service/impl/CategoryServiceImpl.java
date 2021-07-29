package com.alkemy.ong.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;



import javax.persistence.EntityNotFoundException;

import com.alkemy.ong.dto.response.CategoryResponseDto;
import com.alkemy.ong.service.Interface.IFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alkemy.ong.dto.request.CategoryRequestDto;
import com.alkemy.ong.model.Category;
import com.alkemy.ong.repository.CategoryRepository;
import com.alkemy.ong.service.Interface.ICategory;

@Service
public class CategoryServiceImpl implements ICategory {

      private final CategoryRepository ctgRepo;
      private final MessageSource messageSource;
      private final ProjectionFactory projectionFactory;
      private final IFileStore fileStore;

    @Autowired
    public CategoryServiceImpl(CategoryRepository ctgRepo, MessageSource messageSource, ProjectionFactory projectionFactory, IFileStore fileStore) {
        this.ctgRepo = ctgRepo;
        this.messageSource = messageSource;
        this.projectionFactory = projectionFactory;
        this.fileStore = fileStore;
    }


    @Override
      public CategoryResponseDto createCategory(CategoryRequestDto dto)  {
          Category category = new Category(
                  dto.getName(),
                  dto.getDescription()
          );
          Category categoryCreated = ctgRepo.save(category);
          categoryCreated.setImage(fileStore.save(categoryCreated, dto.getImage()));
          return projectionFactory.createProjection(CategoryResponseDto.class, ctgRepo.save(categoryCreated));
      }

      @Override
      public CategoryResponseDto updateCategoryById(Long id, CategoryRequestDto dto) {

          Category updateCategory = findCategoriesById(id);

          if (!dto.getName().isBlank())
              updateCategory.setName(dto.getName());

          if (dto.getImage() != null)
            updateCategory.setImage(fileStore.save(updateCategory, dto.getImage()));

          if (!dto.getDescription().isBlank())
              updateCategory.setDescription(dto.getDescription());

          updateCategory.setEdited(new Date());
          return projectionFactory.createProjection(CategoryResponseDto.class, ctgRepo.save(updateCategory));
      }

      @Override
      public CategoryResponseDto findById(Long id) {
        return projectionFactory.createProjection(CategoryResponseDto.class, findCategoriesById(id));
      }

      @Override
      public List<CategoryResponseDto> findAll() {
        return ctgRepo.findAllProjectedBy();
      }

      @Override
      public String deleteById(Long id) {
          Category category = findCategoriesById(id);
          fileStore.deleteFilesFromS3Bucket(category);
          return messageSource.getMessage(
                "category.delete.successful", null, Locale.getDefault()
          );
      }

      @Override
      public Category findCategoriesById(Long id) {
          return ctgRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(
                  messageSource.getMessage("categories.error.object.notFound", null, Locale.getDefault())));
      }


    public static boolean isNumeric(String nombre){
        boolean resultado;
        try{
            Long.parseLong(nombre);
            resultado = true;
        }catch(NumberFormatException e){
            resultado = false;
        }
        return resultado;
    }

    
	@Override
	public Page<CategoryResponseDto> findAllWithNameInPage(Pageable pageable) {
		Page<CategoryResponseDto> dtoPage = ctgRepo.fetchName(pageable);
		return dtoPage;
	}
}