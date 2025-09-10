package org.Akorad.service;

import org.Akorad.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<Book> getAllBooks(Pageable pageable);
    Book getBookById(Long id);
    Book saveBook (Book book);
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
}
