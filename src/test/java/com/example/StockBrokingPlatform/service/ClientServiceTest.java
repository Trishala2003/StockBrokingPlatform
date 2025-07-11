package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.ClientDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.ClientMapper;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.example.StockBrokingPlatform.model.Client.ClientStatus.ACTIVE;
import static com.example.StockBrokingPlatform.model.Client.KYCStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client();
        client.setId(1L);
        client.setClientCode("CLI001");
        client.setName("Test User");
        client.setEmail("test@example.com");
        client.setPhone("1234567890");
        client.setPan("ABCDE1234F");
        client.setKycStatus(COMPLETED);
        client.setStatus(ACTIVE);

        clientDTO = ClientMapper.toDTO(client);
    }

    @Test
    void testCreateClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        ClientDTO result = clientService.createClient(clientDTO);

        assertNotNull(result);
        assertEquals("CLI001", result.getClientCode());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testGetAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        List<ClientDTO> result = clientService.getAllClients();

        assertEquals(1, result.size());
        assertEquals("CLI001", result.get(0).getClientCode());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testGetClientById_Found() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        ClientDTO result = clientService.getClientById(1L);

        assertEquals("CLI001", result.getClientCode());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById(1L));
    }

    @Test
    void testUpdateClient_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO updatedDTO = clientService.updateClient(1L, clientDTO);

        assertNotNull(updatedDTO);
        assertEquals("CLI001", updatedDTO.getClientCode());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testUpdateClient_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.updateClient(1L, clientDTO));
    }

    @Test
    void testDeleteClient_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);

        assertDoesNotThrow(() -> clientService.deleteClient(1L));
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteClient_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.deleteClient(1L));
    }

    @Test
    void testSearchClients() {
        when(clientRepository.findByNameContainingIgnoreCaseOrClientCodeContainingIgnoreCase("Test", "CLI"))
                .thenReturn(List.of(client));

        List<ClientDTO> result = clientService.searchClients("Test", "CLI");
        assertEquals(1, result.size());
        verify(clientRepository, times(1))
                .findByNameContainingIgnoreCaseOrClientCodeContainingIgnoreCase("Test", "CLI");
    }
}
