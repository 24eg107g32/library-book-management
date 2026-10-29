package com.example.library.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class BookController {
    private final BookService service;

    public BookController(BookService service) { this.service = service; }

    @GetMapping
    public List<Book> all(@RequestParam(required = false) String search) {
        return service.findAll(search);
    }

    @GetMapping("/{bookId}")
    public Book one(@PathVariable String bookId) { return service.findById(bookId); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody Book book) { return service.create(book); }

    @PutMapping("/{bookId}")
    public Book update(@PathVariable String bookId, @Valid @RequestBody Book book) {
        return service.update(bookId, book);
    }

    @PatchMapping("/{bookId}/availability")
    public Book status(@PathVariable String bookId, @RequestBody Map<String, String> body) {
        String raw = body.get("status");
        try {
            return service.updateStatus(bookId, raw == null ? null : BookStatus.valueOf(raw.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status must be AVAILABLE or ISSUED");
        }
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String bookId) { service.delete(bookId); }
}
