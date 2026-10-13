package com.udea.bancoudea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BancoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String customerJson(String account, double balance) {
        return String.format(Locale.US,
                "{\"firstName\":\"Ana\",\"lastName\":\"Perez\",\"accountNumber\":\"%s\",\"balance\":%.2f}",
                account, balance);
    }

    private long createCustomer(String account, double balance) throws Exception {
        String response = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson(account, balance)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void customerCrudFlow() throws Exception {
        long id = createCustomer("CRUD-001", 200.0);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CRUD-001")));

        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("CRUD-001"));

        mockMvc.perform(put("/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson("CRUD-001", 500.0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500.0));

        mockMvc.perform(delete("/api/customers/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void transferMovesMoneyAndAppearsInHistory() throws Exception {
        createCustomer("TRF-FROM", 100.0);
        createCustomer("TRF-TO", 10.0);

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountNumber\":\"TRF-FROM\",\"receiverAccountNumber\":\"TRF-TO\",\"amount\":40.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(40.0));

        mockMvc.perform(get("/api/transactions/account/TRF-FROM"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TRF-TO")));
    }

    @Test
    void transferWithInsufficientBalanceReturnsBadRequest() throws Exception {
        createCustomer("POOR-FROM", 5.0);
        createCustomer("POOR-TO", 10.0);

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountNumber\":\"POOR-FROM\",\"receiverAccountNumber\":\"POOR-TO\",\"amount\":999.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Sender Balance not enough")));
    }

    @Test
    void transferWithUnknownAccountReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountNumber\":\"NOPE-1\",\"receiverAccountNumber\":\"NOPE-2\",\"amount\":1.0}"))
                .andExpect(status().isBadRequest());
    }
}
