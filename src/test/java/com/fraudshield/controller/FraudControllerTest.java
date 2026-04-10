package com.fraudshield.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FraudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void evaluate_validRequest_returns200WithScore() throws Exception {
        String body = """
                {
                    "transactionId": "tx-001",
                    "userId": "user-1",
                    "amount": 100.00,
                    "currency": "USD",
                    "merchantId": "merchant-1",
                    "country": "US",
                    "deviceId": "device-abc",
                    "ipAddress": "192.168.1.1",
                    "cardBin": "411111",
                    "transactionTime": "2026-04-10T12:00:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/fraud/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-001"))
                .andExpect(jsonPath("$.decision").exists())
                .andExpect(jsonPath("$.fraudScore").isNumber())
                .andExpect(jsonPath("$.ruleResults").isArray())
                .andExpect(jsonPath("$.ruleResults[0].ruleType").exists());
    }

    @Test
    void evaluate_missingRequiredFields_returns400() throws Exception {
        String body = """
                {
                    "amount": 100.00
                }
                """;

        mockMvc.perform(post("/api/v1/fraud/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void evaluate_invalidAmount_returns400() throws Exception {
        String body = """
                {
                    "transactionId": "tx-001",
                    "userId": "user-1",
                    "amount": 0.00,
                    "currency": "USD",
                    "merchantId": "merchant-1",
                    "transactionTime": "2026-04-10T12:00:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/fraud/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
