package com.example.bookstore.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    public List<Book> search(String term) {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        return bookRepository.search(term.trim());
    }

    public List<Book> findInStock() {
        return bookRepository.findByStockGreaterThanOrderByTitleAsc(0);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    public boolean existsByIsbnExcludingId(String isbn, Long excludeId) {
        return bookRepository.findAll().stream()
                .anyMatch(b -> b.getIsbn().equals(isbn) && !b.getId().equals(excludeId));
    }

    public long countInStock() {
        return bookRepository.countByStockGreaterThan(0);
    }
}
