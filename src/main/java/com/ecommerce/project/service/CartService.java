package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
