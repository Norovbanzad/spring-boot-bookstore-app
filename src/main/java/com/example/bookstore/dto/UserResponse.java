package com.example.bookstore.dto;

import com.example.bookstore.model.Role;

public record UserResponse (
			
			Long id,
			String firstName,
			String lastName,
			String email,
			Role role,
			boolean enabled) 
{

}
