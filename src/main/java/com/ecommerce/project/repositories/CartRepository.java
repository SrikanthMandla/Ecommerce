package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c from Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c from Cart c WHERE c.user.email = ?1 AND c.cartId = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    Cart findCartByCartId(Long cartId);

    @Query("select c from Cart c  JOIN FETCH c.cartItems ci JOIN FETCH ci.product p where p.productId = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
