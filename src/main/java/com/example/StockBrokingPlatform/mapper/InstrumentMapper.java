package com.example.StockBrokingPlatform.mapper;

import com.example.StockBrokingPlatform.DTO.InstrumentDTO;
import com.example.StockBrokingPlatform.model.Instrument;

public class InstrumentMapper {

    public static InstrumentDTO toDTO(Instrument instrument) {
        InstrumentDTO dto = new InstrumentDTO();
        dto.setId(instrument.getId());
        dto.setSymbol(instrument.getSymbol());
        dto.setCompanyName(instrument.getCompanyName());
        dto.setExchange(instrument.getExchange());
        dto.setExchangeType(instrument.getExchangeType());
        dto.setCurrentPrice(instrument.getCurrentPrice());
        dto.setLotSize(instrument.getLotSize());
        return dto;
    }

    public static Instrument toEntity(InstrumentDTO dto) {
        Instrument instrument = new Instrument();
        instrument.setId(dto.getId());
        instrument.setSymbol(dto.getSymbol());
        instrument.setCompanyName(dto.getCompanyName());
        instrument.setExchange(dto.getExchange());
        instrument.setExchangeType(dto.getExchangeType());
        instrument.setCurrentPrice(dto.getCurrentPrice());
        instrument.setLotSize(dto.getLotSize());
        return instrument;
    }
}
