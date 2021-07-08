package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.ImageSlideCreationDto;
import com.alkemy.ong.dto.ImageSlideDto;
import com.alkemy.ong.exception.InvalidImageException;
import com.alkemy.ong.model.ImageSlide;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImgSlideService {

    ImageSlideDto createSlide(ImageSlideCreationDto imageSlideCreationDto);
    List<ImageSlide> getAll();

    ImageSlideDto updateImage(Long id, ImageSlideCreationDto image) throws InvalidImageException;
    String deleteImage(Long id);
    List<ImageSlideCreationDto> getAllSlidesByOrganization(Long organizationId);

    ImageSlide updateImage(ImageSlide image) throws InvalidImageException;


    ImageSlide getImageSlideById(Long id);
}
