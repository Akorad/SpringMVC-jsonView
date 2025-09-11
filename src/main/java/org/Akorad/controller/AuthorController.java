package org.Akorad.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.Views;
import org.Akorad.entity.Author;
import org.Akorad.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    @JsonView(Views.AuthorSummery.class)
    public List<Author> getAllUsers() {
        return authorService.getAllAuthors();
    }

    @GetMapping("/{id}")
    @JsonView(Views.AuthorDetails.class)
    public Author getUserById(@PathVariable("id") Long id) {
        return authorService.getAuthorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.AuthorSummery.class)
    public Author createUser(@Valid @RequestBody Author author) {
        return authorService.createAuthor(author);
    }

    @PutMapping("/{id}")
    public Author updateUser(@PathVariable("id") Long id, @Valid @RequestBody Author author) {
        return authorService.updateAuthor(id, author);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        authorService.deleteAuthor(id);
    }
}
