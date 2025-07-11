package com.example.StockBrokingPlatform.controller;


import com.example.StockBrokingPlatform.DTO.ClientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.StockBrokingPlatform.model.Client.ClientStatus.ACTIVE;
import static com.example.StockBrokingPlatform.model.Client.KYCStatus.COMPLETED;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientDTO sampleClient;

    @BeforeEach
    void setUp() {
        sampleClient = new ClientDTO();
        sampleClient.setClientCode("CLI001");
        sampleClient.setName("Trishala Singhavi");
        sampleClient.setEmail("trishala@example.com");
        sampleClient.setPhone("9876543210");
        sampleClient.setPan("ABCDE1234F");
        sampleClient.setKycStatus(COMPLETED);
        sampleClient.setStatus(ACTIVE);
    }

    @Test
    void testCreateClient() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Trishala Singhavi")));
    }

    @Test
    void testGetAllClients() throws Exception {
        // First, create a client
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClient)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testGetClientById() throws Exception {
        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClient)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ClientDTO created = objectMapper.readValue(response, ClientDTO.class);

        mockMvc.perform(get("/api/clients/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientCode", is("CLI001")));
    }

    @Test
    void testUpdateClient() throws Exception {
        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClient)))
                .andReturn().getResponse().getContentAsString();

        ClientDTO created = objectMapper.readValue(response, ClientDTO.class);
        created.setName("Updated Name");

        mockMvc.perform(put("/api/clients/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void testDeleteClient() throws Exception {
        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClient)))
                .andReturn().getResponse().getContentAsString();

        ClientDTO created = objectMapper.readValue(response, ClientDTO.class);

        mockMvc.perform(delete("/api/clients/" + created.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchClients() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleClient)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clients/search")
                        .param("name", "trishala"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientCode", is("CLI001")));
    }
}

