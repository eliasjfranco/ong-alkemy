package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class OrganizationDto {

    private String name;

    private String image;

    private String phone;

    private String address;
}