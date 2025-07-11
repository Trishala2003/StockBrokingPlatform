package com.example.StockBrokingPlatform.controller;

import com.example.StockBrokingPlatform.DTO.OrderDTO;
import com.example.StockBrokingPlatform.model.Order;
import com.example.StockBrokingPlatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public OrderDTO placeOrder(@RequestBody OrderDTO dto) {
        return orderService.placeOrder(dto);
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}/modify")
    public OrderDTO modifyOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        return orderService.modifyOrder(id, dto);
    }

    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @GetMapping("/client/{clientId}")
    public List<OrderDTO> getOrdersByClient(@PathVariable Long clientId) {
        return orderService.getOrdersByClientId(clientId);
    }

    @GetMapping("/pending")
    public List<OrderDTO> getPendingOrders() {
        return orderService.getPendingOrders();
    }

    @PutMapping("/{id}/status")
    public OrderDTO updateStatus(@PathVariable Long id, @RequestParam Order.OrderStatus orderStatus) {
        return orderService.updateStatus(id, orderStatus);
    }
}
