package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.OrderDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.model.Order;
import com.example.StockBrokingPlatform.repository.ClientRepository;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import com.example.StockBrokingPlatform.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.StockBrokingPlatform.model.Client.ClientStatus.ACTIVE;
import static com.example.StockBrokingPlatform.model.Client.ClientStatus.INACTIVE;
import static com.example.StockBrokingPlatform.model.Client.KYCStatus.COMPLETED;
import static com.example.StockBrokingPlatform.model.Client.KYCStatus.NOT_COMPLETED;
import static com.example.StockBrokingPlatform.model.Order.OrderStatus.*;
import static com.example.StockBrokingPlatform.model.Order.OrderType.BUY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private InstrumentRepository instrumentRepository;

    @InjectMocks
    private OrderService orderService;

    private Client client;
    private Instrument instrument;
    private Order order;

    @BeforeEach
    void init() {
        client = new Client();
        client.setId(1L);

        instrument = new Instrument();
        instrument.setId(1L);

        order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setInstrument(instrument);
        order.setOrderStatus(PENDING);
    }

    @Test
    void testPlaceOrder_Success() {
        // ✅ Create and setup client with required conditions
        Client client = new Client();
        client.setId(1L);
        client.setStatus(ACTIVE);
        client.setKycStatus(COMPLETED);

        // ✅ Create and setup instrument with required lotSize
        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setLotSize(10);

        // ✅ Setup input order DTO
        OrderDTO dto = new OrderDTO();
        dto.setClientId(1L);
        dto.setInstrumentId(1L);
        dto.setQuantity(10);
        dto.setPrice(100.0);
        dto.setOrderType(BUY);

        // ✅ Mock behavior
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ✅ Call and verify
        OrderDTO result = orderService.placeOrder(dto);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
    }


    @Test
    void testPlaceOrder_ClientNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(dto));
    }

    @Test
    void testPlaceOrder_InstrumentNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(1L);
        dto.setInstrumentId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(instrumentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(dto));
    }

    @Test
    void testPlaceOrder_ClientInactiveOrKycIncomplete_ThrowsException() {
        Client client = new Client();
        client.setId(1L);
        client.setStatus(INACTIVE); // or KYC NOT COMPLETED
        client.setKycStatus(NOT_COMPLETED);

        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setLotSize(10);

        OrderDTO dto = new OrderDTO();
        dto.setClientId(1L);
        dto.setInstrumentId(1L);
        dto.setQuantity(10);
        dto.setPrice(100.0);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        assertThrows(IllegalStateException.class, () -> orderService.placeOrder(dto));
    }

    @Test
    void testPlaceOrder_QuantityNotMultipleOfLotSize_ThrowsException() {
        Client client = new Client();
        client.setId(1L);
        client.setStatus(ACTIVE);
        client.setKycStatus(COMPLETED);

        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setLotSize(10);

        OrderDTO dto = new OrderDTO();
        dto.setClientId(1L);
        dto.setInstrumentId(1L);
        dto.setQuantity(7); // Not multiple of 10
        dto.setPrice(100.0);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(dto));
    }

    @Test
    void testPlaceOrder_InvalidPrice_ThrowsException() {
        Client client = new Client();
        client.setId(1L);
        client.setStatus(ACTIVE);
        client.setKycStatus(COMPLETED);

        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setLotSize(10);

        OrderDTO dto = new OrderDTO();
        dto.setClientId(1L);
        dto.setInstrumentId(1L);
        dto.setQuantity(10);
        dto.setPrice(-1.0); // Invalid

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(dto));
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order, new Order()));
        List<OrderDTO> orders = orderService.getAllOrders();
        assertEquals(2, orders.size());
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderDTO result = orderService.getOrderById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testModifyOrder_Success() {
        OrderDTO dto = new OrderDTO();
        dto.setPrice(200.0);
        dto.setQuantity(20);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO modified = orderService.modifyOrder(1L, dto);

        assertEquals(200.0, modified.getPrice());
        assertEquals(20, modified.getQuantity());
    }

    @Test
    void testModifyOrder_InvalidStatus() {
        order.setOrderStatus(EXECUTED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class, () -> orderService.modifyOrder(1L, new OrderDTO()));
    }

    @Test
    void testCancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.cancelOrder(1L);
        assertEquals("CANCELLED", order.getOrderStatus());
    }

    @Test
    void testCancelOrder_InvalidStatus() {
        order.setOrderStatus(EXECUTED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void testGetOrdersByClientId() {
        when(orderRepository.findByClientId(1L)).thenReturn(Arrays.asList(order, new Order()));
        List<OrderDTO> result = orderService.getOrdersByClientId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPendingOrders() {
        when(orderRepository.findByOrderStatus(PENDING)).thenReturn(List.of(order, new Order()));
        Order incomplete = new Order();
        incomplete.setClient(client);
        incomplete.setInstrument(instrument);
        List<OrderDTO> result = orderService.getPendingOrders();
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO updated = orderService.updateStatus(1L, EXECUTED);

        assertEquals("EXECUTED", updated.getStatus());
    }

    @Test
    void testUpdateStatus_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateStatus(999L, CANCELLED));
    }
}
