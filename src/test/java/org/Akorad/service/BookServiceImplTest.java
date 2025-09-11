package org.Akorad.service;

import org.Akorad.entity.Author;
import org.Akorad.entity.Book;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        author = new Author();
        author.setId(1L);
        author.setName("J. K. Rowling");

        book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setIsbn("1234567890");
        book.setAuthor(author);
    }

    @Test
    void testGetAllBooks_ReturnsPage() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Book> result = bookService.getAllBooks(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Harry Potter");
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Harry Potter");
    }

    @Test
    void testSaveBook_Success() {
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.saveBook(book);

        assertThat(result).isNotNull();
        assertThat(result.getIsbn()).isEqualTo("1234567890");
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBook_Success() {
        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setIsbn("9876543210");
        updatedBook.setAuthor(author);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorService.getAuthorById(author.getId())).thenReturn(author);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.updateBook(1L, updatedBook);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getIsbn()).isEqualTo("9876543210");
        assertThat(result.getAuthor()).isEqualTo(author);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(2L));
    }

    @Test
    void testUpdateBook_NotFound() {
        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setIsbn("9876543210");
        updatedBook.setAuthor(author);

        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(2L, updatedBook));
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(2L));
    }
}
