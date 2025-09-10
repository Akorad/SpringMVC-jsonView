package org.Akorad.service;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Book;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Book bookToUpdate = getBookById(id);
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setIsbn(book.getIsbn());
        bookToUpdate.setAuthor(authorService.getAuthorById(book.getAuthor().getId()));
        return bookRepository.save(bookToUpdate);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}
