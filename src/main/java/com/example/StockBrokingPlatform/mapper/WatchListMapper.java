package com.example.StockBrokingPlatform.mapper;

import com.example.StockBrokingPlatform.DTO.WatchListDTO;
import com.example.StockBrokingPlatform.model.Client;
import com.example.StockBrokingPlatform.model.WatchList;

public class WatchListMapper {
    public static WatchListDTO toDTO(WatchList entity) {
        WatchListDTO dto = new WatchListDTO();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClient().getId());
        dto.setName(entity.getName());
        dto.setDefault(entity.isDefault());
        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }

    public static WatchList toEntity(WatchListDTO dto, Client client) {
        WatchList entity = new WatchList();
        entity.setId(dto.getId());
        entity.setClient(client);
        entity.setName(dto.getName());
        entity.setDefault(dto.isDefault());
        entity.setCreatedDate(dto.getCreatedDate());
        return entity;
    }
}