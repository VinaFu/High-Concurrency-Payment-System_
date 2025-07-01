package com.vina.HighConcurrencyPayemntSystem.payment.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentRequest;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;
import com.vina.HighConcurrencyPayemntSystem.payment.controller.PaymentController;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.PaymentChannel;
import com.vina.HighConcurrencyPayemntSystem.payment.service.PaymentService;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processPayment_Success() throws Exception{
        PaymentRequest request = new PaymentRequest("888", PaymentChannel.PAYPAL);

        PaymentResult mockResult = new PaymentResult(true, "Payment processed successfully", OrderStatus.CREATED);

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(mockResult);

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Payment processed successfully"));
    }

    @Test
    void processPayment_NotFound() throws Exception{
        PaymentRequest request = new PaymentRequest("", PaymentChannel.PAYPAL);
        PaymentResult result = new PaymentResult(false, "Order not found!", OrderStatus.FAILED);

        when(paymentService.processPayment(request)).thenReturn(result);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found!"));
    }
    @Test
    void processPayment_AlreadyPaid() throws Exception{
        PaymentRequest request = new PaymentRequest("999", PaymentChannel.PAYPAL);
        PaymentResult result = new PaymentResult(false,"Order is already paid via PAYPAL", OrderStatus.PAID );

        when(paymentService.processPayment(request)).thenReturn(result);

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order is already paid via PAYPAL"));
    }
    @Test
    void processPayment_NotCreated() throws Exception{
        PaymentRequest request = new PaymentRequest("1010", PaymentChannel.PAYPAL);
        PaymentResult result = new PaymentResult(false,"Order is not in payable status!", OrderStatus.FAILED );

        when(paymentService.processPayment(request)).thenReturn(result);

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order is not in payable status!"));
    }
}
