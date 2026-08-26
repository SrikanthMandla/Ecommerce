package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import jakarta.transaction.Transactional;

import java.util.List;


public interface CartService {


    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCartById(String emailId, Long cartId);


    @Transactional
    CartDTO updateProductQuantitiyInCart(Long productId, int quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCart(Long cartId, Long productId);
}
