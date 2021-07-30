package com.alkemy.ong.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "testimonials")
@SQLDelete(sql = "UPDATE testimonials SET deleted = true where id=?")
@FilterDef(name = "deletedTestimonialsFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedTestimonialsFilter", condition = "deleted = :isDeleted")
@NoArgsConstructor
@Getter @Setter @EqualsAndHashCode
@ApiModel(description = "Modelo de testimonios")
public class Testimony implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identificación única de la entidad")
    private long id;

    @NotBlank(message = "El campo Nombre no debe estar vacío")
    @Column(name = "name", nullable = false, length = 50)
    @ApiModelProperty(notes = "Nombre del testimonio")
    private String name;

    @ApiModelProperty(notes = "Imagen del testimonio")
    private String image;

    @NotBlank(message = "El campo Content no debe estar vacío")
    @Column(name = "content")
    @ApiModelProperty(notes = "Contenido o descripción del testimonio")
    private String content;


    @Column(name = "date_created")
    @ApiModelProperty(notes ="Fecha de creación de testimonio")
    private LocalDateTime created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_edited")
    @ApiModelProperty(notes ="Fecha de actualización de testimonio")
    private Date edited;

    @ApiModelProperty(notes ="Baja de testimonio")
    private Boolean deleted = Boolean.FALSE;

    public Testimony(String name, String content) {
        this.name = name;
        this.content = content;
        this.created = LocalDateTime.now();
    }
}
