package com.example.bookstore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.UserCreateRequest;
import com.example.bookstore.dto.UserResponse;
import com.example.bookstore.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public List<UserResponse> findAll() {
		return userService.findAllUsers();
	}
	
	@PostMapping
	public UserResponse create(@RequestBody UserCreateRequest request) {
		return userService.createUser(request);
	}
}
