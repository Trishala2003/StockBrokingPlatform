package com.example.StockBrokingPlatform.DTO;

import com.example.StockBrokingPlatform.model.Order;

import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private Long clientId;
    private Long instrumentId;
    private Order.OrderType orderType;
    private Integer quantity;
    private Double price;
    private Order.OrderStatus status;
    private LocalDateTime orderDate;
    private Order.Validity validity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Order.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.OrderType orderType) {
        this.orderType = orderType;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public Order.Validity getValidity() {
        return validity;
    }

    public void setValidity(Order.Validity validity) {
        this.validity = validity;
    }
}
