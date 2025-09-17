package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Book;
import org.Akorad.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


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
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        bookRepository.findAll().forEach(book -> bookRepository.delete(book.getId()));
    }

    @Test
    void testCreateBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Harry Potter");
        book.setPublicationYear(1997);
        book.setAuthor("J.K. Rowling");


        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated());

        assertThat(bookRepository.findAll()).hasSize(1);
    }

    @Test
    void testGetBookById_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Harry Potter");
        book.setPublicationYear(1997);
        book.setAuthor("J.K. Rowling");
        book = bookRepository.save(book);

        bookRepository.findAll().forEach(b -> System.out.println(b.getId()));

        mockMvc.perform(get("/api/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Harry Potter"))
                .andExpect(jsonPath("$.publicationYear").value(1997))
                .andExpect(jsonPath("$.author").value("J.K. Rowling"));

    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        mockMvc.perform(get("/api/books/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBooks_WithPagination() throws Exception {
        Book book1 = new Book();
        book1.setTitle("Harry Potter");
        book1.setPublicationYear(1997);
        book1.setAuthor("J.K. Rowling");
        bookRepository.save(book1);
        Book book2 = new Book();
        book2.setTitle("Harry Potter2");
        book2.setPublicationYear(1999);
        book2.setAuthor("J.K. Rowling2");
        bookRepository.save(book2);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()));
    }

    @Test
    void testUpdateBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Harry Potter");
        book.setPublicationYear(1997);
        book.setAuthor("J.K. Rowling");
        book = bookRepository.save(book);

        Book updateRequest = new Book();
        updateRequest.setTitle("New Title");
        updateRequest.setPublicationYear(111);
        updateRequest.setAuthor("author");

        mockMvc.perform(put("/api/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.publicationYear").value(111));

        assertThat(bookRepository.findById(book.getId()).get().getTitle()).isEqualTo("New Title");
    }

    @Test
    void testDeleteBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Harry Potter");
        book.setPublicationYear(1997);
        book.setAuthor("J.K. Rowling");
        book = bookRepository.save(book);

        mockMvc.perform(delete("/api/books/" + book.getId()))
                .andExpect(status().isNoContent());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }
}
