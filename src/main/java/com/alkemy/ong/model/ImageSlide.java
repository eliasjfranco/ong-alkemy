package com.alkemy.ong.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@SQLDelete(sql = " UPDATE image_slide SET deleted = TRUE WHERE id = ?")
@Where(clause = "deleted = false")
public class ImageSlide implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String text;

    @NotNull(message = "Es necesario un valor entero para ordenar las imágenes")
    private Long ordered;
<<<<<<< HEAD

    @NotNull(message = "Es necesaria una Id de organización")
    private Long organizationId;

    private Date createdAt;
    private boolean deleted = false;

    public ImageSlide(String text, Long ordered, Long organizationId, Date createdAt) {
=======
    private Date createdAt;
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    public ImageSlide(String imageUrl, String text, Long ordered, Organization organization, Date createdAt) {
        this.imageUrl = imageUrl;
>>>>>>> e2fcd982cda01f5deb5e6285dbf64aa239492315
        this.text = text;
        this.ordered = ordered;
        this.organization = organization;
        this.createdAt = createdAt;
    }



}
