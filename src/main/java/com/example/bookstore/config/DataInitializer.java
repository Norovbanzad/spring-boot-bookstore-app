package com.example.bookstore.config;

import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedBooks(BookService bookService) {
        return args -> {
            if (bookService.findAll().isEmpty()) {
                List<Book> sampleBooks = List.of(
                        new Book("Pride and Prejudice", "Jane Austen", "978-0141439518",
                                new BigDecimal("9.99"), LocalDate.of(1813, 1, 28),
                                "A classic romance novel following the spirited Elizabeth Bennet.", 12),
                        new Book("1984", "George Orwell", "978-0451524935",
                                new BigDecimal("12.50"), LocalDate.of(1949, 6, 8),
                                "A dystopian social science fiction novel and cautionary tale.", 8),
                        new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084",
                                new BigDecimal("11.25"), LocalDate.of(1960, 7, 11),
                                "The unforgettable novel of a childhood in a sleepy Southern town.", 15),
                        new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565",
                                new BigDecimal("10.99"), LocalDate.of(1925, 4, 10),
                                "The story of the mysteriously wealthy Jay Gatsby.", 6),
                        new Book("Brave New World", "Aldous Huxley", "978-0060850524",
                                new BigDecimal("13.75"), LocalDate.of(1932, 1, 1),
                                "A grim, prescient look at a future where society is engineered.", 0),
                        new Book("The Catcher in the Rye", "J.D. Salinger", "978-0316769488",
                                new BigDecimal("9.50"), LocalDate.of(1951, 7, 16),
                                "The classic novel of teenage alienation and confusion.", 20),
                        new Book("Sapiens", "Yuval Noah Harari", "978-0062316097",
                                new BigDecimal("18.99"), LocalDate.of(2011, 1, 1),
                                "A brief history of humankind from the Stone Age to the present.", 4),
                        new Book("Dune", "Frank Herbert", "978-0441172719",
                                new BigDecimal("15.99"), LocalDate.of(1965, 8, 1),
                                "Set on the desert planet Arrakis, the story of Paul Atreides.", 0)
                );
                bookService.save(sampleBooks.stream().findFirst().get());
                sampleBooks.stream().skip(1).forEach(bookService::save);
                log.info("Seeded {} sample books", bookService.findAll().size());
            }
        };
    }
}
