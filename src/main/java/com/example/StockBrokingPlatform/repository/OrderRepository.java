package com.example.StockBrokingPlatform.repository;

import com.example.StockBrokingPlatform.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long clientId);
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);

}
