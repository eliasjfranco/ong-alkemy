package com.alkemy.ong.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface TestimonialsResponseDto {

    Long getId();
    String getName();
    String getImage();
    String getContent();
    LocalDateTime getCreated();
    Date getEdited();
    Boolean getDeleted();

}
