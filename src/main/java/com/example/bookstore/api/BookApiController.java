package com.example.bookstore.api;

import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService bookService;

    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list(@RequestParam(value = "q", required = false) String q) {
        return bookService.search(q);
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + id));
    }
}
