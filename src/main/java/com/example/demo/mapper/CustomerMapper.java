package com.example.demo.mapper;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.model.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    List<CustomerDTO> toDTOList(List<Customer> customers);
}
