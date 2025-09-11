package org.Akorad.service;

import org.Akorad.entity.Author;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        author = new Author();
        author.setId(1L);
        author.setName("J. K. Rowling");
    }

    @Test
    void testGetAllAuthors_ReturnsList() {
        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<Author> result = authorService.getAllAuthors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("J. K. Rowling");
        verify(authorRepository).findAll();
    }

    @Test
    void testGetAuthorById_Found() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        Author result = authorService.getAuthorById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("J. K. Rowling");
    }

    @Test
    void testCreateAuthor_Success() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author result = authorService.createAuthor(author);

        assertThat(result).isEqualTo(author);
        verify(authorRepository).save(author);
    }

    @Test
    void testUpdateAuthor_Success() {
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Updated Name");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author result = authorService.updateAuthor(1L, updatedAuthor);

        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(authorRepository).save(author);
    }

    @Test
    void testDeleteAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.deleteAuthor(1L);

        verify(authorRepository).delete(author);
    }

    @Test
    void testGetAuthorById_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getAuthorById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 1");
    }

    @Test
    void testUpdateAuthor_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.updateAuthor(1L, author))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 1");
    }

    @Test
    void testDeleteAuthor_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.deleteAuthor(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 1");
    }

}
