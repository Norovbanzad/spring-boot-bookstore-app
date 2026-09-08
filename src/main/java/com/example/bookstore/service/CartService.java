package com.example.bookstore.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bookstore.dto.AddCartItemRequest;
import com.example.bookstore.dto.CartItemResponse;
import com.example.bookstore.dto.CartResponse;
import com.example.bookstore.dto.UpdateCartItemRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CurrentUserService currentUserService;

    public CartService (CartRepository cartRepository, CartItemRepository cartItemRepository, BookRepository bookRepository, CurrentUserService currentUserService) {
        this.bookRepository = bookRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public Cart getOrCreateCurrentCart() {
        User user = currentUserService.getCurrentUser();

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);

            return  cartRepository.save(cart);
        });
    }

    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        Cart cart = getOrCreateCurrentCart();

        Book book = bookRepository.findById(request.bookId()).orElseThrow();

        if(!book.isActive()) {
            System.err.println("Inactive book cannot be added to the cart");
        }

        if(book.getStockQuantity() <= 0) {
            System.err.println("Book is out of stock");
        }  

        System.out.println(book);

        CartItem item = cartItemRepository.findByCartAndBook(cart, book).orElse(null);

        int newQuantity;
        if(item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setBook(book);
            newQuantity = request.quantity();
        } else {
            newQuantity = item.getQuantity() + request.quantity();
        }

        if(newQuantity > book.getStockQuantity()) {
            System.err.println("Requested quantity exceeds stock");
        }

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);

        return  getCurrentCart();
    }

    public CartResponse getCurrentCart() {

        Cart cart = getOrCreateCurrentCart();

        List<CartItem> items = cartItemRepository.findByCartOrderByIdAsc(cart);
        List<CartItemResponse> itemResponses = items.stream().map(this::toResponse).toList();

        BigDecimal total = itemResponses.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), itemResponses, total);

    }

    @Transactional 
    public CartResponse updateQuantity(Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCurrentCart();
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();

        if(!item.getCart().getId().equals(cart.getId())) {
            System.err.println("Cart item not found");
        }

        if(request.quantity() > item.getBook().getStockQuantity()) {
            System.err.println("Requested quantity exceeds stock");
        }

        item.setQuantity(request.quantity());

        cartItemRepository.save(item);
        return getCurrentCart();
    }

    public CartResponse removeItem(Long itemId) {
        Cart cart = getOrCreateCurrentCart();
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();

        if(!item.getCart().getId().equals(cart.getId())) {
            System.err.println("Cart item not found");
        }

        cartItemRepository.delete(item);
        return getCurrentCart();
    }

    private CartItemResponse toResponse(CartItem item) {
        BigDecimal lineTotal = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(item.getId(), item.getBook().getId(),
                                    item.getBook().getTitle(),
                                    item.getBook().getPrice(),
                                item.getQuantity(), lineTotal);
    }
}
