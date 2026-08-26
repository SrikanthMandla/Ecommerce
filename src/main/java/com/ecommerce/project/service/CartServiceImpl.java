package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;



    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //find existing cart or create one
        Cart cart = createCart();

        //Retrive the product details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

       //perform validations
          //--finding_cartItem
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);


           //-checking product exist in cart or not
        if(cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " is already in the cart");
        }

        if(product.getQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is Unavailable");

        }

        if(product.getQuantity() < quantity) {
            throw new APIException("Please make the order " + product.getProductName() + " quantity less than or equal to " + product.getQuantity() + "." );
        }

     //create cartItem
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        cart.getCartItems().add(newCartItem);


        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("no carts found");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOs = cart.getCartItems().stream()
                    .map(item -> {
                        ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(item.getQuantity());
                        return productDTO;
                    }).toList();
            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).toList();
        return cartDTOs;
    }

    @Override
    public CartDTO getCartById(String emailId, Long cartId) {

        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);

        if(cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

//        cart.getCartItems().forEach(cartItem -> {
//            cartItem.getProduct().setQuantity(cartItem.getQuantity());
//        });

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(cartItem -> {
                    ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(cartItem.getQuantity());
                    return productDTO;
                }).toList();

        cartDTO.setProducts(productDTOs);


        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantitiyInCart(Long productId, int quantity) {

        String email = authUtils.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findCartByCartId(cartId);

        if(cart == null) {
            throw new ResourceNotFoundException("Cart", "email", email);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity() ==  0) {
            throw new APIException("Product " + product.getProductName() + " is Unavailable");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if(cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " is not present in the cart");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if(newQuantity < 0) {
            throw new APIException("product quantity cannot be negative");
        }
        if(product.getQuantity() < quantity) {
            throw new APIException("Product " + product.getQuantity() + " units available");
        }

         if(newQuantity == 0) {

             cartItemRepository.deleteCartItemByCartIdAndProductId(cartId, productId);
             return new CartDTO();

         }

        cartItem.setQuantity(newQuantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(cartItem1 -> {
                    ProductDTO productDTO = modelMapper.map(cartItem1.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(cartItem1.getQuantity());
                    return productDTO;

                }).toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;

    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findCartByCartId(cartId);

        if(cart == null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        //cartItemRepository.delete(cartItem);
        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId, productId);

      cartRepository.save(cart);
        return "Product " + cartItem.getProduct().getProductName() + " has been deleted";
    }

    private Cart createCart() {
        Cart userCart  = cartRepository.findCartByEmail(authUtils.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());

        return cartRepository.save(cart);

    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

         Cart cart = cartRepository.findById(cartId)
                 .orElseThrow(()-> new ResourceNotFoundException("Cart", "CartId", cartId));

         CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

         if(cartItem == null){
              throw new APIException("Product " + product.getProductName() + " is not present in the cart");
         }

         double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

          cartItem.setProductPrice(product.getSpecialPrice());
          cartItem.setDiscount(product.getDiscount());

          cartItemRepository.save(cartItem);
          cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * cartItem.getQuantity()));

          cartRepository.save(cart);

    }
}
