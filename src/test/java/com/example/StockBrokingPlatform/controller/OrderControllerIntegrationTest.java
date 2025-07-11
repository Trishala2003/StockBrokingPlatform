package com.example.StockBrokingPlatform.controller;

import com.example.StockBrokingPlatform.DTO.OrderDTO;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.repository.ClientRepository;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import com.example.StockBrokingPlatform.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.example.StockBrokingPlatform.model.Client.ClientStatus.ACTIVE;
import static com.example.StockBrokingPlatform.model.Client.KYCStatus.COMPLETED;
import static com.example.StockBrokingPlatform.model.Instrument.Exchange.NSE;
import static com.example.StockBrokingPlatform.model.Instrument.ExchangeType.Equity;
import static com.example.StockBrokingPlatform.model.Order.OrderType.BUY;
import static com.example.StockBrokingPlatform.model.Order.OrderType.SELL;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long clientId;
    private Long instrumentId;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        clientRepository.deleteAll();
        instrumentRepository.deleteAll();

        Client client = new Client();
        client.setName("Test Client");
        client.setPhone("9999999999");
        client.setClientCode("T12345");
        client.setStatus(ACTIVE); // Required for placing order
        client.setKycStatus(COMPLETED);
        clientId = clientRepository.save(client).getId();

        Instrument instrument = new Instrument();
        instrument.setSymbol("TCS");
        instrument.setCompanyName("Tata Consultancy Services");
        instrument.setExchange(NSE);
        instrument.setExchangeType(Equity);
        instrument.setCurrentPrice(3500.0);
        instrument.setLotSize(1);
        instrumentId = instrumentRepository.save(instrument).getId();
    }

    @Test
    void testPlaceOrder_Success() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(100.0);
        dto.setQuantity(5);
        dto.setOrderType(BUY);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetAllOrders() throws Exception {
        // First, place an order
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(150.0);
        dto.setQuantity(10);
        dto.setOrderType(SELL);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetOrderById() throws Exception {
        // First place an order
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(120.0);
        dto.setQuantity(3);
        dto.setOrderType(BUY);

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        OrderDTO createdOrder = objectMapper.readValue(response, OrderDTO.class);

        mockMvc.perform(get("/api/orders/" + createdOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdOrder.getId()));
    }

    @Test
    void testModifyOrder() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(130.0);
        dto.setQuantity(8);
        dto.setOrderType(BUY);

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        OrderDTO created = objectMapper.readValue(response, OrderDTO.class);

        created.setPrice(200.0);
        created.setQuantity(15);

        mockMvc.perform(put("/api/orders/" + created.getId() + "/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(200.0))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    @Test
    void testCancelOrder() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(100.0);
        dto.setQuantity(2);
        dto.setOrderType(SELL);

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        OrderDTO order = objectMapper.readValue(response, OrderDTO.class);

        mockMvc.perform(delete("/api/orders/" + order.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testGetPendingOrders() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(400.0);
        dto.setQuantity(1);
        dto.setOrderType(BUY);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void testUpdateStatus() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(180.0);
        dto.setQuantity(7);
        dto.setOrderType(BUY);

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        OrderDTO order = objectMapper.readValue(response, OrderDTO.class);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/status")
                        .param("orderStatus", "EXECUTED")) // <-- ✅ Correct param name
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXECUTED"));
    }

    @Test
    void testGetOrdersByClient() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setClientId(clientId);
        dto.setInstrumentId(instrumentId);
        dto.setPrice(999.0);
        dto.setQuantity(3);
        dto.setOrderType(SELL);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/client/" + clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value(clientId));
    }
}
