package com.ecommerce.project.repositories;

import com.ecommerce.project.payload.CartItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemDTO,Long> {


}
