package org.Akorad.service;

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

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setPublicationYear(1997);
        book.setAuthor("J.K. Rowling");
    }

    @Test
    void testGetAllBooks_ReturnsList() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Harry Potter");
        verify(bookRepository, times(1)).findAll();
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

        bookService.saveBook(book);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBook_Success() {
        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setPublicationYear(1852);
        updatedBook.setAuthor("author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.update(any(Book.class))).thenReturn(book);

        bookService.updateBook(1L, updatedBook);

        assertThat(book.getTitle()).isEqualTo("New Title");
        assertThat(book.getPublicationYear()).isEqualTo(1852);
        assertThat(book.getAuthor()).isEqualTo("author");
        verify(bookRepository, times(1)).update(book);
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.delete(1L)).thenReturn(1);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.delete(2L)).thenReturn(0);

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(2L));
    }

    @Test
    void testUpdateBook_NotFound() {
        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setPublicationYear(1999);
        updatedBook.setAuthor("author");

        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(2L, updatedBook));
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(2L));
    }
}
