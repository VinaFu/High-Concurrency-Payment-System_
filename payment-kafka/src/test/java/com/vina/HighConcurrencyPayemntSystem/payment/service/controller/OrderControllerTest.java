package com.vina.HighConcurrencyPayemntSystem.payment.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vina.HighConcurrencyPayemntSystem.payment.controller.OrderController;
import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.GlobalExceptionHandler;
import com.vina.HighConcurrencyPayemntSystem.payment.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createdOrder_CreateOrder() throws Exception{
        Order order = new Order();
        order.setOrderNumber("444");
        order.setStatus(OrderStatus.CREATED);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setOrderNumber("444");
        savedOrder.setStatus(OrderStatus.CREATED);

        when(orderService.createOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("444"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void findByOrderNumber_GetMethodWithOrderNumber() throws Exception{
        Order order = new Order();
        order.setOrderNumber("555");
        order.setStatus(OrderStatus.CREATED);

        when(orderService.findByOrderNumber("555")).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/555"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("555"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void findByOrderNumber_GetMethodWithNull() throws Exception{
        when(orderService.findByOrderNumber("000")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found: 000"))
                .andExpect(jsonPath("$.orderStatus").value("FAILED"));
    }

    @Test
    void updateOrderStatus_PutSuccess() throws Exception{
        Order saveOrder = new Order();
        saveOrder.setOrderNumber("666");
        saveOrder.setStatus(OrderStatus.CREATED);

        when(orderService.findByOrderNumber("666")).thenReturn(Optional.of(saveOrder));
        doNothing().when(orderService).updateOrderStatus("666", OrderStatus.PROCESSING);

        mockMvc.perform(put("/api/orders/666/status")
                        .param("status", "PROCESSING"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateOrderStatus_PutNotFound() throws Exception {
        when(orderService.findByOrderNumber("777")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/orders/777/status")
                        .param("status", "PROCESSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found: 777"))
                .andExpect(jsonPath("$.orderStatus").value("FAILED"));
    }
}
