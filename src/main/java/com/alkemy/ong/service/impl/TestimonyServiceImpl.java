package com.alkemy.ong.service.impl;
import com.alkemy.ong.dto.request.TestimonyRequestDto;
import com.alkemy.ong.dto.response.TestimonialsResponseDto;
import com.alkemy.ong.model.Testimony;
import com.alkemy.ong.repository.TestimonyRepository;
import com.alkemy.ong.service.Interface.IFileStore;
import com.alkemy.ong.service.Interface.ITestimony;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Locale;

@Service
public class TestimonyServiceImpl implements ITestimony {

    @Autowired
    private final TestimonyRepository testimonyRepository;
    private final ProjectionFactory projectionFactory;
    private final IFileStore fileStore;
    private final MessageSource messageSource;

    @Autowired
    public TestimonyServiceImpl(TestimonyRepository testimonyRepository, ProjectionFactory projectionFactory, IFileStore fileStore, MessageSource messageSource) {
        this.testimonyRepository = testimonyRepository;
        this.projectionFactory = projectionFactory;
        this.fileStore = fileStore;
        this.messageSource = messageSource;
    }



    @Override
    public Testimony getTestimonialsById(Long id) {
        return testimonyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        messageSource.getMessage("testimonials.error.not.found", null, Locale.getDefault())
                )
        );
    }


    @Override
    public String deleteById(Long id) {
        Testimony testimony = getTestimonialsById(id);
        fileStore.deleteFilesFromS3Bucket(testimony);
        testimonyRepository.delete(testimony);
        return messageSource.getMessage("testimonials.delete.successful",null, Locale.getDefault());
    }



    @Override
    public TestimonialsResponseDto createTestimonials(TestimonyRequestDto testimonyRequestDto) {

        Testimony testimony = new Testimony(
                testimonyRequestDto.getName(),
                testimonyRequestDto.getContent()
        );
        Testimony testimonyCreated = testimonyRepository.save(testimony);

        testimonyCreated.setImage(fileStore.save(testimonyCreated, testimonyRequestDto.getImage()));

        return projectionFactory.createProjection(TestimonialsResponseDto.class, testimonyRepository.save(testimonyCreated));
    }

    @Override
    public TestimonialsResponseDto updateTestimonials(Long id, TestimonyRequestDto testimonyRequestDto) {
        Testimony testimony = getTestimonialsById(id);
        testimony.setContent(testimonyRequestDto.getContent());
        testimony.setName(testimonyRequestDto.getName());
        
        if(testimonyRequestDto.getImage() != null)
            testimony.setImage(fileStore.save(testimony, testimonyRequestDto.getImage()));

        return projectionFactory.createProjection(TestimonialsResponseDto.class, testimonyRepository.save(testimony));
    }

    @Override
    public Page<Testimony> showAllTestimonials(Pageable pageable) {
        return testimonyRepository.findAll(pageable);
    }

}
