package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.Long;
import java.lang.String;
import java.util.List;
import java.util.function.LongSupplier;
import org.springframework.aot.generate.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.DeclaredQuery;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.support.PageableExecutionUtils;

/**
 * AOT generated JPA repository implementation for {@link BookRepository}.
 */
@Generated
public class BookRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public BookRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link BookRepository#existsByIsbn(java.lang.String)}.
   */
  public boolean existsByIsbn(String isbn) {
    String queryString = "SELECT b.id FROM Book b WHERE b.isbn = :isbn";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("isbn", isbn);
    query.setMaxResults(1);

    return !query.getResultList().isEmpty();
  }

  /**
   * AOT generated implementation of {@link BookRepository#existsByIsbnAndIdNot(java.lang.String,java.lang.Long)}.
   */
  public boolean existsByIsbnAndIdNot(String isbn, Long id) {
    String queryString = "SELECT b.id FROM Book b WHERE b.isbn = :isbn AND b.id != :id";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("isbn", isbn);
    query.setParameter("id", id);
    query.setMaxResults(1);

    return !query.getResultList().isEmpty();
  }

  /**
   * AOT generated implementation of {@link BookRepository#findByActiveTrue(org.springframework.data.domain.Pageable)}.
   */
  public Page<Book> findByActiveTrue(Pageable pageable) {
    String queryString = "SELECT b FROM Book b WHERE b.active = TRUE";
    String countQueryString = "SELECT COUNT(b) FROM Book b WHERE b.active = TRUE";
    Pageable pageable_1 = pageable != null ? pageable : Pageable.unpaged();
    if (pageable_1.getSort().isSorted()) {
      DeclaredQuery declaredQuery = DeclaredQuery.jpqlQuery(queryString);
      queryString = rewriteQuery(declaredQuery, pageable_1.getSort(), Book.class);
    }
    Query query = this.entityManager.createQuery(queryString);
    if (pageable_1.isPaged()) {
      query.setFirstResult(Long.valueOf(pageable_1.getOffset()).intValue());
      query.setMaxResults(pageable_1.getPageSize());
    }
    LongSupplier countAll = () -> {
      Query countQuery = this.entityManager.createQuery(countQueryString);
      return getCount(countQuery);
    };

    return PageableExecutionUtils.getPage((List<Book>) query.getResultList(), pageable_1, countAll);
  }
}
