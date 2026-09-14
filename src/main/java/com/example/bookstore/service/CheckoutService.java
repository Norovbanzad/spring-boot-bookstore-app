package com.example.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.dto.CheckoutResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.OrderStatus;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessRuleException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;

@Service 
public class CheckoutService {
    
    private final CurrentUserService currentUserService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    // Constructor Injection - Spring framework deer Dependency Injection
    public CheckoutService(BookRepository bookRepository, CartItemRepository cartItemRepository, CartRepository cartRepository, CurrentUserService currentUserService, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.currentUserService = currentUserService;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional // If error occurs, rollback will be performed in the DB.
    public CheckoutResponse checkout() {
        User user = currentUserService.getCurrentUser();

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> {
            throw new BusinessRuleException("Cart does not exist");
        });

        List<CartItem> cartItems = cartItemRepository.findByCartOrderByIdAsc(cart);
        if(cartItems.isEmpty()) {
            throw  new BusinessRuleException("Cart is empty");
        }

        // Validate all cart items
        for(CartItem item : cartItems) {
            Book book = item.getBook();
            if(!book.isActive()) {
                throw new BusinessRuleException("Book is not available" + book.getTitle());
            }

            if(item.getQuantity() > book.getStockQuantity()) {
                throw new BusinessRuleException("Not enough stock" + book.getTitle());
            }
        }    
        
        // Calculate total amount of cart items
        // FOR loop calculate amount of cart items

        // BigDecimal total = BigDecimal.ZERO;
        // for(CartItem item: cartItems) {
        //     Book book = item.getBook();
        //     BigDecimal totalAmountPrice = BigDecimal.valueOf(item.getQuantity()).multiply(book.getPrice());
        //     total.add(totalAmountPrice);
        // }

        //stream -> map -> reduce
        BigDecimal totalAmount = cartItems.stream().map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);

        // Create order items
        for (CartItem item: cartItems) {
            Book book = item.getBook();
            BigDecimal unitPrice = book.getPrice();
            BigDecimal totalLine = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBook(book);
            orderItem.setBookTitle(book.getTitle());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setLineTotal(totalLine);

            orderItemRepository.save(orderItem);

            // Reduce book stock
            book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
            bookRepository.save(book);
        }

        // Empty cart, because order is created.
        cartItemRepository.deleteAll(cartItems);

        return new CheckoutResponse(savedOrder.getId(), 
                                    savedOrder.getStatus().name(), 
                                    savedOrder.getTotalAmount(),
                                    savedOrder.getCreatedAt());
    }



}
