package com.example.bookstore.web;

import com.example.bookstore.book.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredBooks", bookService.findInStock().stream().limit(3).toList());
        model.addAttribute("totalBooks", bookService.findAll().size());
        model.addAttribute("booksInStock", bookService.countInStock());
        return "index";
    }
}
