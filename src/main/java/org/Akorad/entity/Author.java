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
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.AuthorSummery.class})
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @JsonView({Views.AuthorSummery.class})
    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({Views.AuthorDetails.class})
    private java.util.List<Book> books;
}
