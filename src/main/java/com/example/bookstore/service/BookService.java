package com.example.bookstore.service;

import com.example.bookstore.BookstoreApplication;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.entity.Author;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;

@Service
public class BookService {
    private final BookstoreApplication bookstoreApplication;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,
            AuthorRepository authorRepository, BookstoreApplication bookstoreApplication) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.bookstoreApplication = bookstoreApplication;
    }

    public List<BookResponse> findAllBooks() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new RuntimeException("Book already exists with ISBN: " + request.isbn());
        }

        Category foundCategory = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));

        Author foundAuthor = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + request.authorId()));

        Book newBook = new Book();
        newBook.setIsbn(request.isbn());
        newBook.setTitle(request.title());
        newBook.setPrice(request.price());
        newBook.setStockQuantity(request.stockQuantity());
        newBook.setActive(request.active());
        newBook.setCategory(foundCategory);
        newBook.setAuthor(foundAuthor);

        Book savedBook = bookRepository.save(newBook);
        return toResponse(savedBook);
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book foundBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));

        Category category = categoryRepository.findById(id).orElseThrow(()-> new RuntimeException("Category not found with ID: " + request.categoryId()));

        Author author = authorRepository.findById(id).orElseThrow(()-> new RuntimeException("Author not found with ID: " + request.authorId()));

        if (bookRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new RuntimeException("Another book already uses ISBN: " + request.isbn());
        }

        foundBook.setTitle(request.title());
        foundBook.setActive(request.active());
        foundBook.setAuthor(author);
        foundBook.setCategory(category);
        foundBook.setIsbn(request.isbn());
        foundBook.setPrice(request.price());
        foundBook.setStockQuantity(request.stockQuantity());

        Book updatedBook = bookRepository.save(foundBook);

        return toResponse(updatedBook);
    }

    public void deleteBook(Long id) {
         Book foundBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
         bookRepository.delete(foundBook);
    }

    // Private methods are always below the public ones.
    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                book.getStockQuantity(),
                book.isActive(),
                book.getCategory().getId(),
                book.getCategory().getName(),
                book.getAuthor().getId(),
                book.getAuthor().getFirstName(),
                book.getAuthor().getLastName());
    }
}
