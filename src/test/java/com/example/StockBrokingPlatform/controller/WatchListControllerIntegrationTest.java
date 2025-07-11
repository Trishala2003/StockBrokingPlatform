package com.example.StockBrokingPlatform.controller;

import com.example.StockBrokingPlatform.DTO.WatchListDTO;
import com.example.StockBrokingPlatform.DTO.WatchListItemDTO;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.repository.ClientRepository;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import com.example.StockBrokingPlatform.repository.WatchListRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.StockBrokingPlatform.model.Instrument.Exchange.NSE;
import static com.example.StockBrokingPlatform.model.Instrument.ExchangeType.Equity;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertFalse;

@SpringBootTest
@AutoConfigureMockMvc
class WatchListControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private WatchListRepository watchListRepository;

    @Autowired
    private EntityManager entityManager;

    private Long clientId;
    private Long instrumentId;

    @BeforeEach
    void setup() {
        // Save test client
        Client client = new Client();
        client.setName("Test Client");
        client.setPhone("9876543210");
        client.setClientCode("CL123");
        client = clientRepository.save(client);
        clientId = client.getId();

        // Save test instrument
        Instrument instrument = new Instrument();
        instrument.setSymbol("TCS");
        instrument.setCompanyName("Tata Consultancy Services");
        instrument.setExchange(NSE);
        instrument.setExchangeType(Equity);
        instrument.setCurrentPrice(3500.0);
        instrument.setLotSize(1);
        instrument = instrumentRepository.save(instrument);
        instrumentId = instrument.getId();
    }

    @Test
    void testCreateWatchList() throws Exception {
        WatchListDTO dto = new WatchListDTO();
        dto.setName("Tech Watchlist");
        dto.setClientId(clientId);

        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tech Watchlist"))
                .andExpect(jsonPath("$.clientId").value(clientId));
    }

    @Test
    void testGetWatchListById() throws Exception {
        Long id = createWatchList("Test List", true);

        mockMvc.perform(get("/api/watchlists/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test List"));
    }

    @Test
    void testGetClientWatchLists() throws Exception {
        createWatchList("Watch A", true);
        createWatchList("Watch B", true);

        mockMvc.perform(get("/api/watchlists/client/" + clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testUpdateWatchList() throws Exception {
        Long id = createWatchList("Old Name", true);

        WatchListDTO update = new WatchListDTO();
        update.setName("Updated Name");
        update.setClientId(clientId);

        mockMvc.perform(put("/api/watchlists/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testDeleteWatchList() throws Exception {
        Long id = createWatchList("Delete Me", false);

        mockMvc.perform(delete("/api/watchlists/" + id))
                .andExpect(status().isOk());

        assertFalse(watchListRepository.existsById(id), "Watchlist was not actually deleted from DB");

        // Check and print actual response
        mockMvc.perform(get("/api/watchlists/" + id))
                .andDo(result -> {
                    System.out.println("GET Response Status: " + result.getResponse().getStatus());
                    System.out.println("GET Response Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDefaultWatchList_ShouldFail() throws Exception {
        // Only one default watchlist exists
        Long defaultId = createWatchList("Default List", true);

        mockMvc.perform(delete("/api/watchlists/" + defaultId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Default watchlist cannot be deleted")));
    }


    @Test
    void testAddInstrumentToWatchList() throws Exception {
        Long watchListId = createWatchList("Instrument List", true);

        WatchListItemDTO itemDTO = new WatchListItemDTO();
        itemDTO.setInstrumentId(instrumentId);
        itemDTO.setAddedDate(LocalDateTime.now());

        mockMvc.perform(post("/api/watchlists/" + watchListId + "/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrumentId").value(instrumentId));
    }

    @Test
    void testGetWatchListWithItems() throws Exception {
        Long watchListId = createWatchList("With Items", true);

        WatchListItemDTO itemDTO = new WatchListItemDTO();
        itemDTO.setInstrumentId(instrumentId);
        itemDTO.setAddedDate(LocalDateTime.now());

        mockMvc.perform(post("/api/watchlists/" + watchListId + "/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/watchlists/" + watchListId + "/with-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void testRemoveInstrumentFromWatchList() throws Exception {
        Long watchListId = createWatchList("To Remove", true);

        WatchListItemDTO itemDTO = new WatchListItemDTO();
        itemDTO.setInstrumentId(instrumentId);
        itemDTO.setAddedDate(LocalDateTime.now());

        mockMvc.perform(post("/api/watchlists/" + watchListId + "/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/watchlists/" + watchListId + "/instruments/" + instrumentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/watchlists/" + watchListId + "/with-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void testGetWatchListSummary() throws Exception {
        Long id = createWatchList("Summary Watchlist", true);

        mockMvc.perform(get("/api/watchlists/" + id + "/summary"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Summary")));
    }

    // Utility method to create a watchlist and return ID
    // Utility method to create a watchlist and return ID
    private Long createWatchList(String name, boolean isDefault) throws Exception {
        WatchListDTO dto = new WatchListDTO();
        dto.setName(name);
        dto.setClientId(clientId);
        dto.setDefault(isDefault);

        String response = mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, WatchListDTO.class).getId();
    }

}

