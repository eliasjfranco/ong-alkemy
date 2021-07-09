package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ImageSlideDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String imageUrl;
    private String text;
    private Long organizationId;
    private Long ordered;
    private Date createdAt;

}
