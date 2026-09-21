package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Cart API", description = "APIs for managing carts")
@RequestMapping("/api")
public class CartController {
    @Autowired
    AuthUtils authUtils;

    @Autowired
    CartRepository cartRepository;


     @Autowired
     private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {

     CartDTO cartDTO =  cartService.addProductToCart(productId, quantity);

     return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);

    }


    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {

        List<CartDTO> cartDTOS = cartService.getAllCarts();

        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){

        String emailId = authUtils.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
      CartDTO cartDTO = cartService.getCartById(emailId,cartId);

      return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    @Operation(summary = "update product quantity - use 'increase' or 'delete'")
    public ResponseEntity<CartDTO> updateProductQuantity(@Parameter(
            description = "ID of the product to update",
            example = "1",
            required = true
    ) @PathVariable Long productId, @PathVariable String operation) {

            CartDTO cartDTO = cartService.updateProductQuantitiyInCart(productId,
                    operation.equalsIgnoreCase("delete") ? -1 : 1);

            return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }


    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {

        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<>(status, HttpStatus.OK);

    }

}
