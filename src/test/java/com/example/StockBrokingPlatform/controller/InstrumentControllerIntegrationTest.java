package com.example.StockBrokingPlatform.controller;

import com.example.StockBrokingPlatform.DTO.InstrumentDTO;
import com.example.StockBrokingPlatform.mapper.InstrumentMapper;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import com.example.StockBrokingPlatform.repository.OrderRepository;
import com.example.StockBrokingPlatform.repository.WatchListItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.StockBrokingPlatform.model.Instrument.Exchange.BSE;
import static com.example.StockBrokingPlatform.model.Instrument.Exchange.NSE;
import static com.example.StockBrokingPlatform.model.Instrument.ExchangeType.Equity;
import static com.example.StockBrokingPlatform.model.Instrument.ExchangeType.Futures;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InstrumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private WatchListItemRepository watchListItemRepository;

    @Autowired
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        watchListItemRepository.deleteAll();
        orderRepository.deleteAll();
        instrumentRepository.deleteAll();
    }


    private InstrumentDTO createSampleInstrumentDTO(String symbol, String companyName, Instrument.ExchangeType exchangeType, Instrument.Exchange exchange) {
        InstrumentDTO dto = new InstrumentDTO();
        dto.setSymbol(symbol);
        dto.setCompanyName(companyName);
        dto.setExchange(exchange);
        dto.setExchangeType(exchangeType);
        dto.setCurrentPrice(2500.00);
        dto.setLotSize(1);
        return dto;
    }

    @Test
    void testAddInstrumentAndGetAll() throws Exception {
        InstrumentDTO dto = createSampleInstrumentDTO("TCS", "Tata Consultancy Services", Equity, NSE);

        mockMvc.perform(post("/api/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("TCS"));

        mockMvc.perform(get("/api/instruments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].companyName", hasItem("Tata Consultancy Services")));
    }

    @Test
    void testGetInstrumentById_Success() throws Exception {
        InstrumentDTO dto = createSampleInstrumentDTO("INFY", "Infosys", Equity, NSE);
        Instrument saved = instrumentRepository.save(InstrumentMapper.toEntity(dto));

        mockMvc.perform(get("/api/instruments/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Infosys"));
    }

    @Test
    void testGetInstrumentById_NotFound() throws Exception {
        mockMvc.perform(get("/api/instruments/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateInstrument() throws Exception {
        InstrumentDTO dto = createSampleInstrumentDTO("HDFCBANK", "HDFC Bank Ltd", Equity, BSE);
        Instrument saved = instrumentRepository.save(InstrumentMapper.toEntity(dto));

        InstrumentDTO updateDTO = createSampleInstrumentDTO("HDFCBANK", "HDFC Bank Updated", Equity, BSE);
        updateDTO.setCurrentPrice(1600.00);
        updateDTO.setLotSize(2);

        mockMvc.perform(put("/api/instruments/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("HDFC Bank Updated"))
                .andExpect(jsonPath("$.exchange").value("BSE"))
                .andExpect(jsonPath("$.currentPrice").value(1600.00))
                .andExpect(jsonPath("$.lotSize").value(2));
    }

    @Test
    void testSearchBySymbolOrCompanyName() throws Exception {
        InstrumentDTO dto = createSampleInstrumentDTO("RELIANCE", "Reliance Industries", Equity, NSE);
        instrumentRepository.save(InstrumentMapper.toEntity(dto));

        mockMvc.perform(get("/api/instruments/search")
                        .param("symbol", "RELI")
                        .param("companyName", "Reliance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("RELIANCE"));
    }

    @Test
    void testGetByExchangeType() throws Exception {
        InstrumentDTO dto1 = createSampleInstrumentDTO("TATAMOTORS", "Tata Motors", Equity, NSE);
        InstrumentDTO dto2 = createSampleInstrumentDTO("ICICIBANK", "ICICI Bank", Futures, BSE);

        instrumentRepository.saveAll(List.of(
                InstrumentMapper.toEntity(dto1),
                InstrumentMapper.toEntity(dto2)
        ));

        mockMvc.perform(get("/api/instruments/exchange-type/Equity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].symbol", hasItem("TATAMOTORS")))
                .andExpect(jsonPath("$[*].symbol", Matchers.not(hasItem("ICICIBANK"))));
    }
}
