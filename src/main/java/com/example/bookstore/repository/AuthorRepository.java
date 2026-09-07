package com.example.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookstore.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

	//findAll()
	// save()
	// delete()
	//count()
	//existById
}
