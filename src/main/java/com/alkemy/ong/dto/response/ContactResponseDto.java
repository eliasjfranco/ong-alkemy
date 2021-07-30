package com.alkemy.ong.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface ContactResponseDto {
    Long getId();
    String getName();
    String getMessage();
    String getPhone();
    String getEmail();
    Boolean getDeleted();
}
