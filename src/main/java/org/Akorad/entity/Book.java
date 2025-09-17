package org.Akorad.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Book {
    @Id
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String title;

    @NotBlank(message = "Автор не может быть пустым")
    private String author;

    @NotBlank(message = "Год публикации не может быть пустым")
    private Integer publicationYear;
}
