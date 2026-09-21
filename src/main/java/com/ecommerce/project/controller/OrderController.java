package com.ecommerce.project.controller;


import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.utils.AuthUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Order API", description = "APIs for managing Orders")
@RequestMapping("/api")
public class OrderController {
   @Autowired
   private AuthUtils authUtils;

   @Autowired
   private OrderService orderService;


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,@RequestBody OrderRequestDTO orderRequestDTO){
        String emailId = authUtils.loggedInEmail();

        OrderDTO orderDTO = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );

   return new ResponseEntity<>(orderDTO, HttpStatus.OK);

    }
}
