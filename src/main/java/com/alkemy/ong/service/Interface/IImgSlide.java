package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.request.ImageSlideRequestDto;
import com.alkemy.ong.dto.response.ImageSlideResponseDto;
import com.alkemy.ong.model.ImageSlide;

import java.util.List;

public interface IImgSlide {

    ImageSlideResponseDto createSlide(ImageSlideRequestDto imageSlideRequestDto);
    List<ImageSlideResponseDto> getAll();

    ImageSlideResponseDto updateImage(Long id, ImageSlideRequestDto image);
    String deleteImage(Long id);
    List<ImageSlideResponseDto> getAllSlidesByOrganization(Long organizationId);

    ImageSlide getImageSlideById(Long id);
}
