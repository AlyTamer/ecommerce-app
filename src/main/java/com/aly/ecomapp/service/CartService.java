package com.aly.ecomapp.service;


import com.aly.ecomapp.exception.*;
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


    private CartRepository cartRepository;
    private static final int MAX_ITEMS = 10;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


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

    public CartDTO createCartForUser(Long userId) {
        if (userId == null) {
            throw new UserException(UserExceptionMessages.NULL_USER_ID);
        }
        if (cartRepository.findByUserId(userId) != null) {
            throw new CartException(CartExceptionMessages.USER_ALREADY_HAS_CART);
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(List.of());
        Cart saved;
        try {
            saved = cartRepository.save(cart);
        } catch (Exception e) {
            throw new CartException(CartExceptionMessages.FAILED_TO_CREATE_CART,e);
        }
        return toDTO(saved);
    }

    public CartDTO getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartExceptionMessages.CART_NOT_FOUND));
        return toDTO(cart);
    }

    // get cart by user ID
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new CartException(CartExceptionMessages.CART_NOT_FOUND);
        }
        return toDTO(cart);
    }

    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CartDTO updateCart(Long cartId, List<CartItemDTO> itemDTOs) {
        if (itemDTOs == null) {
            throw new CartItemException(CartItemExceptionMessages.EMPTY_CART);
        }
        if (itemDTOs.size() > MAX_ITEMS) {
            throw new CartException(CartExceptionMessages.ITEM_LIMIT);
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartExceptionMessages.CART_NOT_FOUND));

        List<CartItem> items=new ArrayList<>();
        for (CartItemDTO dto : itemDTOs) {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(dto.getProductId());
            cartItem.setQuantity(dto.getQuantity());
            cartItem.setPrice(dto.getPrice());
            items.add(cartItem);
        }

        cart.getItems().clear();
        cart.getItems().addAll(items);
        Cart updated;
        try {
            updated = cartRepository.save(cart);
        } catch (Exception e) {
            throw new CartException(CartExceptionMessages.FAILED_TO_UPDATE_CART, e);
        }
        return toDTO(updated);
    }

    public CartDTO initiateCheckout(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartExceptionMessages.CART_NOT_FOUND));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CartException(CartExceptionMessages.EMPTY_CART);
        }

        return toDTO(cart);
    }

    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartExceptionMessages.CART_NOT_FOUND));

        cart.getItems().clear();
        try {
            cartRepository.save(cart);
        } catch (Exception e) {
            throw new CartException(CartExceptionMessages.FAILED_TO_CLEAR_CART,e);
        }
    }

    public void deleteCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new CartException(CartExceptionMessages.CART_NOT_FOUND);
        }
        try {
            cartRepository.deleteById(cartId);
        } catch (Exception e) {
            throw new CartException(CartExceptionMessages.FAILED_TO_DELETE_CART,e);
        }
    }
}



