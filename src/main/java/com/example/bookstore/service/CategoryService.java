package com.example.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.CategoryRepository;

@Service
public class CategoryService {
	
	//private final BookstoreApplication bookstoreApplication;
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	};
	
	public Category findCategoryById(Long id) {
		return categoryRepository.findById(id).orElseThrow();
	}
	
	// Create
	@Transactional
	public Category create(Category category) {
		return categoryRepository.save(category);
	}
	
	// Update
	public Category updateCategory(Long id, Category newCategory) {
		Category foundCategory = categoryRepository.findById(id).orElseThrow();
		foundCategory.setName(newCategory.getName());
		return categoryRepository.save(foundCategory);
	}
	
	// Delete
	public void deleteCategory(Long id) {
		Category founCategory = categoryRepository.findById(id).orElseThrow();
		categoryRepository.delete(founCategory);
	}
}
