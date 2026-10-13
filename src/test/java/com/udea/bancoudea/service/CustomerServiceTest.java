package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.mapper.CustomerMapper;
import com.udea.bancoudea.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);
        customerService = new CustomerService(customerRepository, mapper);
    }

    private Customer customer(Long id, String account, double balance) {
        return new Customer(id, account, "Ana", "Perez", balance);
    }

    @Test
    void getAllCustomerReturnsMappedList() {
        when(customerRepository.findAll()).thenReturn(List.of(customer(1L, "A1", 100.0), customer(2L, "A2", 50.0)));

        List<CustomerDTO> result = customerService.getAllCustomer();

        assertEquals(2, result.size());
        assertEquals("A1", result.get(0).getAccountNumber());
    }

    @Test
    void getCustomerByIdFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer(1L, "A1", 100.0)));

        CustomerDTO dto = customerService.getCustomerById(1L);

        assertEquals(1L, dto.getId());
        assertEquals(100.0, dto.getBalance());
    }

    @Test
    void getCustomerByIdNotFound() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(9L));
        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    @Test
    void createCustomerSavesAndReturnsDto() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(5L);
            return c;
        });

        CustomerDTO dto = customerService.createCustomer(new CustomerDTO(null, "Luis", "Gomez", "A5", 300.0));

        assertEquals(5L, dto.getId());
        assertEquals("A5", dto.getAccountNumber());
    }

    @Test
    void updateCustomerChangesFields() {
        Customer existing = customer(1L, "A1", 100.0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerDTO dto = customerService.updateCustomer(1L, new CustomerDTO(null, "Maria", "Lopez", "A1", 999.0));

        assertEquals("Maria", dto.getFirstName());
        assertEquals(999.0, dto.getBalance());
    }

    @Test
    void updateCustomerNotFound() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());
        CustomerDTO body = new CustomerDTO(null, "X", "Y", "A9", 1.0);

        assertThrows(RuntimeException.class, () -> customerService.updateCustomer(9L, body));
    }

    @Test
    void deleteCustomerOk() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomerNotFound() {
        when(customerRepository.existsById(9L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> customerService.deleteCustomer(9L));
    }
}
