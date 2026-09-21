package com.ecommerce.project.controller;


import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Address API", description = "APIs for managing address")
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AddressService addressService;
     @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){

      AddressDTO savedAddressDTO = addressService.createAddress(addressDTO);

      return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }
    @GetMapping("/address")
    public  ResponseEntity<List<AddressDTO>> getAllAddresses(){
         List<AddressDTO> addressDTOs = addressService.getAllAddresses();
         return new ResponseEntity<>(addressDTOs,HttpStatus.OK);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
         AddressDTO addressDTO = addressService.getAddressById(addressId);
         return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.OK);
     }

     @GetMapping("/users/address")
     public ResponseEntity<List<AddressDTO>> getAddressByLoggedInUser(){
         List<AddressDTO> addressDTOs = addressService.getAddressByLoggedInUser();
         return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
     }
     @PutMapping("/address/{id}")
     public ResponseEntity<AddressDTO> updateAddress(@PathVariable long id, @RequestBody AddressDTO addressDTO){
         AddressDTO updatedAddressDTO = addressService.updateAddress(id, addressDTO);
         return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
     }
    @DeleteMapping("/addresses/{id}")
     public ResponseEntity<String> deleteAddress(@PathVariable long id){
         String status = addressService.deleteAddress(id);
         return new ResponseEntity<>(status, HttpStatus.OK);
     }
}

