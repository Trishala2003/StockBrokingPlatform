package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.ClientDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.ClientMapper;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Create a new client
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = ClientMapper.toEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return ClientMapper.toDTO(savedClient);
    }

    // Get all clients
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get a client by ID
    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
        return ClientMapper.toDTO(client);
    }

    // Update a client
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        existingClient.setClientCode(clientDTO.getClientCode());
        existingClient.setName(clientDTO.getName());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setPhone(clientDTO.getPhone());
        existingClient.setPan(clientDTO.getPan());
        existingClient.setKycStatus(clientDTO.getKycStatus());
        existingClient.setStatus(clientDTO.getStatus());

        Client updatedClient = clientRepository.save(existingClient);
        return ClientMapper.toDTO(updatedClient);
    }

    // Delete a client
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
        clientRepository.delete(client);
    }

    public List<ClientDTO> searchClients(String name, String clientCode) {
        List<Client> clients = clientRepository.findByNameContainingIgnoreCaseOrClientCodeContainingIgnoreCase(name,clientCode);
        return clients.stream().map(ClientMapper::toDTO).collect(Collectors.toList());
    }
}
