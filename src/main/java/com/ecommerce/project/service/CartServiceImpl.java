package com.ecommerce.project.service;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    AuthUtils authUtils;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //find existing cart or create one
        Cart cart = createCart();

        return null;
    }

    private Cart createCart() {
        Cart userCart  = cartRepository.findCartByEmail(authUtils.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());
        Cart newcart = cartRepository.save(cart);
        return newcart;

    }
}
