package com.example.library.book;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) { this.repository = repository; }

    public List<Book> findAll(String search) {
        if (search == null || search.isBlank()) return repository.findAllByOrderByTitleAsc();
        String term = search.trim();
        return repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(term, term);
    }

    public Book findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + id));
    }

    public Book create(Book book) {
        if (repository.existsById(book.getBookId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book ID already exists");
        }
        if (book.getStatus() == null) book.setStatus(BookStatus.AVAILABLE);
        try {
            return repository.save(book);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book ID already exists");
        }
    }

    public Book update(String id, Book input) {
        Book existing = findById(id);
        existing.setTitle(input.getTitle());
        existing.setAuthor(input.getAuthor());
        existing.setCategory(input.getCategory());
        if (input.getStatus() != null) existing.setStatus(input.getStatus());
        return repository.save(existing);
    }

    public Book updateStatus(String id, BookStatus status) {
        if (status == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        Book book = findById(id);
        book.setStatus(status);
        return repository.save(book);
    }

    public void delete(String id) {
        Book book = findById(id);
        repository.delete(book);
    }
}
