package com.alkemy.ong.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter @Setter
public class ContactRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El campo Name no puede estar vacío")
    private String name;

    private String phone;

    @NotBlank(message = "El campo Email no puede estar vacío")
    private String email;

    private String message;
}