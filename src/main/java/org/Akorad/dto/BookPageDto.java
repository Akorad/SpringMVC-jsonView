package org.Akorad.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.Akorad.entity.Book;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookPageDto {

    @JsonView(Views.AuthorDetails.class)
    private List<Book> content;

    @JsonView(Views.AuthorDetails.class)
    private long totalElements;

    @JsonView(Views.AuthorDetails.class)
    private int totalPage;

    @JsonView(Views.AuthorDetails.class)
    private int number;

    @JsonView(Views.AuthorDetails.class)
    private int size;
}
