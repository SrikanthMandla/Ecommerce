package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;


@Override
public AddressDTO createAddress(AddressDTO addressDTO){
    User user = authUtils.loggedInUser();
    Address address = modelMapper.map(addressDTO, Address.class);

    List<Address> addressList = user.getAddresses();
    addressList.add(address);
    user.setAddresses(addressList);

    address.setUser(user);
    //userRepository.save(user);
   Address savedaddress =  addressRepository.save(address);

  return modelMapper.map(savedaddress,AddressDTO.class);

   }

   @Override
    public List<AddressDTO> getAllAddresses(){
    List<Address> addressList = addressRepository.findAll();

    List<AddressDTO> addressDTOs = addressList.stream()
            .map(address -> {
                AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
                return addressDTO;
            }).toList();

       return addressDTOs;
   }

   @Override
    public AddressDTO getAddressById(Long id){
    Address address = addressRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Address", "id", id));
       return modelMapper.map(address, AddressDTO.class);
   }

   @Override
   public List<AddressDTO> getAddressByLoggedInUser(){
    User user = authUtils.loggedInUser();

    List<Address> addressList = user.getAddresses();

    List<AddressDTO> addressDTOs = addressList.stream()
            .map(address -> {
                AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
                return addressDTO;
            }).collect(Collectors.toList());

    return addressDTOs;

   }
   @Override
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO){
    Address address = addressRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Address","id", id));

    if(addressDTO.getStreet() != null)
        address.setStreet(addressDTO.getStreet());

    if(addressDTO.getBuildingName() != null)
        address.setBuildingName(addressDTO.getBuildingName());

     if(addressDTO.getCity() != null)
         address.setCity(addressDTO.getCity());

     if(addressDTO.getState() != null)
         address.setState(addressDTO.getState());

     if(addressDTO.getCountry() != null)
         address.setCountry(addressDTO.getCountry());

     if(addressDTO.getPincode() != null)
         address.setPincode(addressDTO.getPincode());

    Address savedAddress = addressRepository.save(address);

    User user = address.getUser();

   user.getAddresses().removeIf(address_Itr -> address_Itr.getAddressId().equals(id));
    user.getAddresses().add(savedAddress);
    userRepository.save(user);
    return modelMapper.map(savedAddress, AddressDTO.class);
   }

   @Override
    public String deleteAddress(Long id){

    Address address = addressRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Address", "id", id));

    User user = address.getUser();
    user.getAddresses().removeIf(address_Itr -> address_Itr.getAddressId().equals(id));
    userRepository.save(user);

    addressRepository.delete(address);
    return "Address with id " + id + " has been deleted";
   }


}
