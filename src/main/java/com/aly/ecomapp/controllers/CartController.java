package com.aly.ecomapp.controllers;
import com.aly.ecomapp.dto.CartDTO;
import com.aly.ecomapp.dto.CartItemDTO;
import com.aly.ecomapp.security.AllowedUser;
import com.aly.ecomapp.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllowedUser
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new cart for a user",
        description = "Creates a new cart for the specified user ID. " +
                      "This endpoint is accessible only to users with ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestParam Long userId) {
        CartDTO cartDTO = cartService.createCartForUser(userId);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get cart by ID",
        description = "Retrieves the cart details for the specified cart ID. " +
                      "This endpoint is accessible only to users with ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long id) {
        CartDTO cartDTO = cartService.getCartById(id);
        return ResponseEntity.ok(cartDTO);
    }
    @Operation(
        summary = "Get cart by user ID",
        description = "Retrieves the cart details for the specified user ID. " +
                      "This endpoint is accessible to all authenticated users.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Long userId) {
        CartDTO cartDTO = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all carts",
        description = "Retrieves a list of all carts in the system. " +
                      "This endpoint is accessible only to users with ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> carts = cartService.getAllCarts();
        return ResponseEntity.ok(carts);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update cart",
        description = "Updates the items in the specified cart. " +
                      "This endpoint is accessible only to users with ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable Long id, @RequestBody List<CartItemDTO> items) {
        CartDTO updated = cartService.updateCart(id, items);
        return ResponseEntity.ok(updated);
    }


    @Operation(
        summary = "Add item to cart",
        description = "Adds an item to the specified cart. " +
                      "This endpoint is accessible to all authenticated users.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}/checkout")
    public ResponseEntity<CartDTO> checkout(@PathVariable Long id) {
        CartDTO cartData = cartService.initiateCheckout(id);
        return ResponseEntity.ok(cartData);
    }


@Operation(
        summary ="Clears all items in the cart",
        description = "Clears all items in the specified cart. " +
                      "This endpoint is accessible to all authenticated users.",
        security = @SecurityRequirement(name="bearerAuth")
)
    @DeleteMapping("/{id}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long id) {
        cartService.clearCart(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary ="Deletes entire cart",
            security = @SecurityRequirement(name="bearerAuth")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

}
