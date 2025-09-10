package org.Akorad.service;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Author;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }

    @Override
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Author updateAuthor(Long id, Author author) {
        Author authorToUpdate = getAuthorById(id);
        authorToUpdate.setName(author.getName());
        authorToUpdate.setBooks(author.getBooks());
        return  authorRepository.save(authorToUpdate);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);
        authorRepository.delete(author);
    }
}
