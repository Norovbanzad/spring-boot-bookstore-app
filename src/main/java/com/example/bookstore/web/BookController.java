package com.example.bookstore.web;

import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("books", bookService.search(q));
        model.addAttribute("q", q);
        return "books/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        model.addAttribute("book", book);
        return "books/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("book") Book book,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "books/form";
        }

        if (bookService.existsByIsbnExcludingId(book.getIsbn(), book.getId())) {
            result.rejectValue("isbn", "duplicate", "A book with this ISBN already exists");
            return "books/form";
        }

        boolean isNew = book.getId() == null;
        bookService.save(book);
        String action = isNew ? "added" : "updated";
        redirectAttributes.addFlashAttribute("flashMessage", "Book " + action + " successfully.");
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteById(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Book deleted successfully.");
        return "redirect:/books";
    }
}
