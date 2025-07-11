// OrderService.java
package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.OrderDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.OrderMapper;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.model.Order;
import com.example.StockBrokingPlatform.repository.ClientRepository;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import com.example.StockBrokingPlatform.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.StockBrokingPlatform.model.Order.OrderStatus.CANCELLED;
import static com.example.StockBrokingPlatform.model.Order.OrderStatus.PENDING;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    public OrderDTO placeOrder(OrderDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Instrument instrument = instrumentRepository.findById(dto.getInstrumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found"));

        // Validate client KYC and status
        if (client.getKycStatus() != Client.KYCStatus.COMPLETED ||
                client.getStatus() != Client.ClientStatus.ACTIVE) {
            throw new IllegalStateException("Client must be ACTIVE and KYC COMPLETED to place an order");
        }
        // Validate quantity
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Order quantity must be a positive number");
        }
        if (instrument.getLotSize() != null && dto.getQuantity() % instrument.getLotSize() != 0) {
            throw new IllegalArgumentException("Order quantity must be in multiples of lot size (" + instrument.getLotSize() + ")");
        }

        // Validate price
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Order price must be a positive number");
        }

        dto.setOrderDate(LocalDateTime.now());
        dto.setStatus(Order.OrderStatus.valueOf("PENDING"));
        Order order = OrderMapper.toEntity(dto, client, instrument);
        return OrderMapper.toDTO(orderRepository.save(order));
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public OrderDTO modifyOrder(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus()!= PENDING) {
            throw new IllegalStateException("Only PENDING orders can be modified. Current status: " + order.getOrderStatus());
        }

        order.setPrice(dto.getPrice());
        order.setQuantity(dto.getQuantity());
        return OrderMapper.toDTO(orderRepository.save(order));
    }

    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus()!= PENDING) {
            throw new IllegalStateException("Only PENDING orders can be canceled. Current status: " + order.getOrderStatus());
        }

        order.setOrderStatus(CANCELLED);
        orderRepository.save(order);
    }

    public List<OrderDTO> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getPendingOrders() {
        return orderRepository.findByOrderStatus(PENDING).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO updateStatus(Long id, Order.OrderStatus orderStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setOrderStatus(orderStatus);
        return OrderMapper.toDTO(orderRepository.save(order));
    }
}