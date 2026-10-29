package com.example.library.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books", uniqueConstraints = @UniqueConstraint(columnNames = "book_id"))
public class Book {
    @Id
    @Column(name = "book_id", nullable = false, unique = true, length = 40)
    @NotBlank(message = "Book ID is required")
    @Size(max = 40, message = "Book ID must be 40 characters or fewer")
    private String bookId;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be 150 characters or fewer")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 120, message = "Author must be 120 characters or fewer")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "Category is required")
    @Size(max = 80, message = "Category must be 80 characters or fewer")
    @Column(nullable = false)
    private String category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status = BookStatus.AVAILABLE;

    public Book() {}

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId == null ? null : bookId.trim(); }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title == null ? null : title.trim(); }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author == null ? null : author.trim(); }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category == null ? null : category.trim(); }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}
