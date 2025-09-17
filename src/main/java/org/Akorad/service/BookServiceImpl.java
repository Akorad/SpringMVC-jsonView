package org.Akorad.service;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Book;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Книга не найдена по id: " + id));
    }

    @Override
    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book book) {
        Book bookToUpdate = getBookById(id);
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setAuthor(book.getAuthor());
        bookToUpdate.setPublicationYear(book.getPublicationYear());
        return bookRepository.update(bookToUpdate);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(id);
    }
}
