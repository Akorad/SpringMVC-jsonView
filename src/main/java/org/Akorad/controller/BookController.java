package org.Akorad.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.Views;
import org.Akorad.entity.Book;
import org.Akorad.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.AuthorDetails.class)
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookService.getAllBooks(pageable);
    }

    // Получение книги по id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.AuthorDetails.class)
    public Book getBookById(@PathVariable("id") Long id) {
        return bookService.getBookById(id);
    }

    // Создание новой книги
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.AuthorDetails.class)
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    // Обновление книги
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.AuthorDetails.class)
    public Book updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    // Удаление книги
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
    }
}
