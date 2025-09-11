package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Author;
import org.Akorad.entity.Book;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    private ObjectMapper objectMapper;

    private Author author1;
    private Author author2;
    private Book book;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        book = new Book();
        book.setId(100L);
        book.setTitle("Book1");
        book.setIsbn("1234567890");

        author1 = new Author();
        author1.setId(1L);
        author1.setName("J. K. Rowling");
        author1.setBooks(List.of(book));

        author2 = new Author();
        author2.setId(2L);
        author2.setName("George R. R. Martin");
    }

    @Test
    void testGetAllAuthorsSuccess() throws Exception {
        List<Author> authors = List.of(author1, author2);
        when(authorService.getAllAuthors()).thenReturn(authors);

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(authors.size()))
                .andExpect(jsonPath("$[0].name").value("J. K. Rowling"))
                .andExpect(jsonPath("$[1].name").value("George R. R. Martin"))
                .andExpect(jsonPath("$[0].books").doesNotExist());
    }

    @Test
    void testGetAuthorByIdSuccess() throws Exception {
        when(authorService.getAuthorById(1L)).thenReturn(author1);

        mockMvc.perform(get("/api/authors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("J. K. Rowling"));
    }

    @Test
    void testCreateAuthorSuccess() throws Exception {
        when(authorService.createAuthor(any(Author.class))).thenReturn(author1);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("J. K. Rowling"))
                .andExpect(jsonPath("$.books").doesNotExist());
    }

    @Test
    void testUpdateAuthorSuccess() throws Exception {
        Author updatedAuthor = new Author();
        updatedAuthor.setId(1L);
        updatedAuthor.setName("J. K. Rowling Updated");
        when(authorService.updateAuthor(eq(1L), any(Author.class))).thenReturn(updatedAuthor);

        mockMvc.perform(put("/api/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("J. K. Rowling Updated"));
    }

    @Test
    void testDeleteAuthorSuccess() throws Exception {
        doNothing().when(authorService).deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAuthorByIdNotFound() throws Exception {
        when(authorService.getAuthorById(1L))
                .thenThrow(new ResourceNotFoundException("Author not found"));

        mockMvc.perform(get("/api/authors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Author not found")));
    }

    @Test
    void testUpdateAuthorNotFound() throws Exception {
        when(authorService.updateAuthor(eq(1L), any(Author.class)))
                .thenThrow(new ResourceNotFoundException("Author not found"));

        mockMvc.perform(put("/api/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author1)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Author not found")));
    }

    @Test
    void testDeleteAuthorNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Author not found"))
                .when(authorService).deleteAuthor(1L);

        mockMvc.perform(delete("/api/authors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Author not found")));
    }

    @Test
    void testCreateAuthorValidationFail() throws Exception {
        Author invalidAuthor = new Author(); // пустое имя

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthor)))
                .andExpect(status().isBadRequest());
    }
}
