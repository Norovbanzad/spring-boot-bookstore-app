package com.example.bookstore.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :term, '%'))
               OR b.isbn LIKE CONCAT('%', :term, '%')
            ORDER BY b.title ASC
            """)
    List<Book> search(@Param("term") String term);

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findByStockGreaterThanOrderByTitleAsc(int stock);

    long countByStockGreaterThan(int stock);
}
