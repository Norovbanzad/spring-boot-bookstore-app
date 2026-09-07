package com.example.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.entity.Author;
import com.example.bookstore.repository.AuthorRepository;

@Service
public class AuthorService {

	private final AuthorRepository authorRepository;
	
	public AuthorService(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}
	
	// findAllAuthors
	public List<Author> findAll() {
		return authorRepository.findAll();
	}
	
	// findAuthorById
	public Author findById(Long id) {
		return authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Author not found, id: " + id));
	}
	
	// createAuthor
	@Transactional
	public Author createAuthor(Author author) {
		return authorRepository.save(author);
	}
	
	// update
	public Author updateAuthor(Long id, Author newAuthor) {
		Author foundAuthor = authorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Author not found: id: " + id));
		foundAuthor.setFirstName(newAuthor.getFirstName());
		foundAuthor.setLastName(newAuthor.getLastName());
		foundAuthor.setBio(newAuthor.getBio());
		return authorRepository.save(foundAuthor);
	}
	
	// delete 
	public void deleteAuthor(Long id) {
		Author foundAuthor = authorRepository.findById(id).orElseThrow();
		authorRepository.delete(foundAuthor);
		
	}
	
}
