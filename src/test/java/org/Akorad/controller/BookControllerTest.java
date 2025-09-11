package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Author;
import org.Akorad.entity.Book;
import org.Akorad.repository.AuthorRepository;
import org.Akorad.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Author author;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        author = new Author();
        author.setName("J. K. Rowling");
        author = authorRepository.save(author);
    }

    @Test
    void testCreateBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Harry Potter");
        book.setIsbn("1234567890");
        book.setAuthor(author);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Harry Potter"))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.author.id").value(author.getId()));

        assertThat(bookRepository.findAll()).hasSize(1);
    }

    @Test
    void testGetBookById_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("987654321");
        book.setAuthor(author);
        book = bookRepository.save(book);

        mockMvc.perform(get("/api/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.isbn").value("987654321"));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        mockMvc.perform(get("/api/books/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBooks_WithPagination() throws Exception {
        Book book1 = new Book();
        book1.setTitle("Book One");
        book1.setIsbn("111");
        book1.setAuthor(author);
        Book book2 = new Book();
        book2.setTitle("Book Two");
        book2.setIsbn("222");
        book2.setAuthor(author);
        bookRepository.saveAll(List.of(book1, book2));

        mockMvc.perform(get("/api/books?page=0&size=1&sort=title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Book One"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testUpdateBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Old Title");
        book.setIsbn("000");
        book.setAuthor(author);
        book = bookRepository.save(book);

        Book updateRequest = new Book();
        updateRequest.setTitle("New Title");
        updateRequest.setIsbn("111");
        updateRequest.setAuthor(author);

        mockMvc.perform(put("/api/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.isbn").value("111"));

        assertThat(bookRepository.findById(book.getId()).get().getTitle()).isEqualTo("New Title");
    }

    @Test
    void testDeleteBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Delete Me");
        book.setIsbn("999");
        book.setAuthor(author);
        book = bookRepository.save(book);

        mockMvc.perform(delete("/api/books/" + book.getId()))
                .andExpect(status().isNoContent());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }
}
