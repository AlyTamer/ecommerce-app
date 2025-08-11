package com.aly.ecomapp.service;


import com.aly.ecomapp.repository.CartRepository;
import com.aly.ecomapp.dto.CartDTO;
import com.aly.ecomapp.dto.CartItemDTO;
import com.aly.ecomapp.entity.Cart;
import com.aly.ecomapp.entity.CartItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    private static final int MAX_ITEMS = 10; // cart limit 10

    // calculate total price
    public BigDecimal calculateTotal(List<CartItem> items) {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private CartDTO toDTO(Cart cart) {
        if (cart == null) {
            throw new CartException(CartExceptionMessages.CART_NOT_FOUND);
        }
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList());
        BigDecimal total = calculateTotal(cart.getItems());
        return new CartDTO(cart.getId(), cart.getUserId(), itemDTOs, total);
    }

    // one cart for each user
    public CartDTO createCartForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (cartRepository.findByUserId(userId) != null) {
            throw new RuntimeException("User already has a cart.");
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(List.of());
        Cart saved = cartRepository.save(cart);
        return toDTO(saved);
    }

    // get cart by ID
    public CartDTO getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));
        return toDTO(cart);
    }

    // get cart by user ID
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("No cart found for user ID: " + userId);
        }
        return toDTO(cart);
    }

    // get All Carts
    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //  update Cart to add or change items
    public CartDTO updateCart(Long cartId, List<CartItemDTO> itemDTOs) {
        if (itemDTOs == null) {
            throw new IllegalArgumentException("Item list cannot be null");
        }
        if (itemDTOs.size() > MAX_ITEMS) {
            throw new RuntimeException("Cannot add more than " + MAX_ITEMS + " items to the cart.");
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));

        List<CartItem> items=new ArrayList<>();
        for (CartItemDTO dto : itemDTOs) {
            CartItem cartItem = new CartItem();
//            cartItem.setId(dto.getId());
            cartItem.setProductId(dto.getProductId());
            cartItem.setQuantity(dto.getQuantity());
            cartItem.setPrice(dto.getPrice());
            items.add(cartItem);
        }

//        List<CartItem> items = itemDTOs.stream().map(dto -> {
//            CartItem item = new CartItem();
//            item.setId(dto.getId());
//            item.setProductId(dto.getProductId());
//            item.setQuantity(dto.getQuantity());
//            item.setPrice(dto.getPrice());
//            return item;
//        }).collect(Collectors.toList());

//        cart.setItems(items);
//        cart.setItems(items);
        cart.getItems().clear();
        cart.getItems().addAll(items);
        Cart updated = cartRepository.save(cart);
        return toDTO(updated);
    }

    //for checkout
    public CartDTO initiateCheckout(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout: Cart is empty");
        }

        return toDTO(cart); // Reuse CartDTO to checkout
    }

    // clear cart
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setItems(List.of());
        cartRepository.save(cart);
    }

    // delete cart
    public void deleteCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Cart not found with ID: " + cartId);
        }
        cartRepository.deleteById(cartId);
    }
}



