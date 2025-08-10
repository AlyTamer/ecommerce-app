package com.aly.ecomapp;

import com.aly.ecomapp.carts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartTestDrive {
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private List<CartItem> items;
    private CartItem item1;
    private CartItem item2;

    @BeforeEach
    void setUp() {
        // Set up test data
        item1 = new CartItem();
        item1.setId(1L);
        item1.setProductId(501L);
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("29.99"));

        item2 = new CartItem();
        item2.setId(2L);
        item2.setProductId(502L);
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("15.50"));

        items = List.of(item1, item2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(101L);
        cart.setItems(new ArrayList<>(items));
    }


    // test to creating cart
    @Test
    void shouldCreateCartForNewUser() {

        when(cartRepository.findByUserId(101L)).thenReturn(null); // No existing cart
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO result = cartService.createCartForUser(101L);

        assertNotNull(result);
        assertEquals(101L, result.getUserId());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("75.48"), result.getTotal());
        verify(cartRepository).save(any(Cart.class));
    }

    // test to crate cart if there's a cart already
    @Test
    void shouldNotCreateCart_WhenUserAlreadyHasCart() {

        when(cartRepository.findByUserId(101L)).thenReturn(cart);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.createCartForUser(101L);
        });
        assertEquals("User already has a cart.", thrown.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    // test to make sure that item limit is less than 10
    @Test
    void shouldUpdateCart_Success() {

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(101L);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        List<CartItemDTO> newItemDTOs = List.of(
                new CartItemDTO(null, 601L, 1, new BigDecimal("10.00"))
        );

        CartDTO result = cartService.updateCart(1L, newItemDTOs);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("10.00"), result.getTotal());
        verify(cartRepository).save(any(Cart.class));
    }

    //if items >10 >>>>no more items can be added
    @Test
    void shouldNotUpdateCart_WhenMoreThan10Items() {
        List<CartItemDTO> over10Items = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            over10Items.add(new CartItemDTO(null, (long) (500 + i), 1, new BigDecimal("10.00")));
        }

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.updateCart(1L, over10Items);
        });

        assertEquals("Cannot add more than 10 items to the cart.", thrown.getMessage());

        // Optional: Verify findById was NOT called
        verify(cartRepository, never()).findById(anyLong());
    }

    // test to check calculations (gettotal methode)
    @Test
    void shouldCalculateTotalCorrectly() {
        BigDecimal total = cartService.calculateTotal(cart.getItems());

        BigDecimal expected = new BigDecimal("75.48"); //just expected: (2 × 29.99) + (1 × 15.50) = 59.98 + 15.50 = 75.48

        assertEquals(expected, total);
    }

    // test to checkout
    @Test
    void shouldInitiateCheckout_Success() {

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.initiateCheckout(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("75.48"), result.getTotal());
    }

    // test to checkout if cart is empty
    @Test
    void shouldNotCheckout_EmptyCart() {

        Cart emptyCart = new Cart();
        emptyCart.setId(2L);
        emptyCart.setUserId(102L);
        emptyCart.setItems(new ArrayList<>());

        when(cartRepository.findById(2L)).thenReturn(Optional.of(emptyCart));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.initiateCheckout(2L);
        });
        assertEquals("Cannot checkout: Cart is empty", thrown.getMessage());
    }




}
