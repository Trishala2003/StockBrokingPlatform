package com.example.StockBrokingPlatform.mapper;


import com.example.StockBrokingPlatform.DTO.ClientDTO;
import com.example.StockBrokingPlatform.model.Client;

public class ClientMapper {

    public static ClientDTO toDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setClientCode(client.getClientCode());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setPan(client.getPan());
        dto.setKycStatus(Client.KYCStatus.valueOf(client.getKycStatus().name()));
        dto.setStatus(Client.ClientStatus.valueOf(client.getStatus().name()));
        return dto;
    }

    public static Client toEntity(ClientDTO dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setClientCode(dto.getClientCode());
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setPan(dto.getPan());
        client.setKycStatus(dto.getKycStatus());
        client.setStatus(dto.getStatus());
        return client;
    }
}

