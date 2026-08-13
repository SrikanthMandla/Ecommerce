package com.ecommerce.project.repositories;

import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.payload.CartItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Query("select ci from CartItem ci where ci.cart.id = ?1 and ci.product.id = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);
}
