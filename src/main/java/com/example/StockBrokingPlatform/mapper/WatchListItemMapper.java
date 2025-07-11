package com.example.StockBrokingPlatform.mapper;

import com.example.StockBrokingPlatform.DTO.WatchListItemDTO;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.model.WatchList;
import com.example.StockBrokingPlatform.model.WatchListItem;

public class WatchListItemMapper {
    public static WatchListItemDTO toDTO(WatchListItem entity) {
        WatchListItemDTO dto = new WatchListItemDTO();
        dto.setId(entity.getId());
        dto.setWatchListId(entity.getWatchList() != null ? entity.getWatchList().getId() : null);
        dto.setInstrumentId(entity.getInstrument().getId());
        dto.setAddedDate(entity.getAddedDate());
        return dto;
    }

    public static WatchListItem toEntity(WatchListItemDTO dto, WatchList watchList, Instrument instrument) {
        WatchListItem entity = new WatchListItem();
        entity.setId(dto.getId());
        entity.setWatchList(watchList);
        entity.setInstrument(instrument);
        entity.setAddedDate(dto.getAddedDate());
        return entity;
    }
}