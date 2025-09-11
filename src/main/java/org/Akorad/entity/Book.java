package org.Akorad.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.Akorad.dto.Views;

@Entity
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Book {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @JsonView({Views.AuthorDetails.class})
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @JsonView({Views.AuthorDetails.class})
    private String title;

    @NotBlank(message = "ISBN is mandatory")
    @JsonView({Views.AuthorDetails.class})
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonView({Views.AuthorDetails.class})
    private Author author;
}
