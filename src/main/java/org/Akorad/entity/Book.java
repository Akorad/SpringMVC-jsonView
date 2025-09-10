package org.Akorad.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.Akorad.dto.Views;

@Entity
@Getter
@Setter
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
    private Author author;
}
