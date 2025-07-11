// InstrumentService.java
package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.InstrumentDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.InstrumentMapper;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class InstrumentService {

    @Autowired
    private InstrumentRepository instrumentRepository;

    public List<InstrumentDTO> getAllInstruments(int page, int size) {
        Page<Instrument> instrumentPage = instrumentRepository.findAll(PageRequest.of(page, size));
        return instrumentPage.stream().map(InstrumentMapper::toDTO).collect(Collectors.toList());
    }

    public InstrumentDTO getInstrumentById(Long id) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found with ID: " + id));
        return InstrumentMapper.toDTO(instrument);
    }

    public InstrumentDTO addInstrument(InstrumentDTO dto) {
        Instrument instrument = InstrumentMapper.toEntity(dto);
        return InstrumentMapper.toDTO(instrumentRepository.save(instrument));
    }

    public InstrumentDTO updateInstrument(Long id, InstrumentDTO dto) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found with ID: " + id));

        instrument.setCompanyName(dto.getCompanyName());
        instrument.setSymbol(dto.getSymbol());
        instrument.setExchange(dto.getExchange());
        instrument.setExchangeType(dto.getExchangeType());
        instrument.setCurrentPrice(dto.getCurrentPrice());
        instrument.setLotSize(dto.getLotSize());

        return InstrumentMapper.toDTO(instrumentRepository.save(instrument));
    }


    public List<InstrumentDTO> searchBySymbolOrCompanyName(String symbol, String companyName) {
        List<Instrument> instruments = instrumentRepository.findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(symbol,companyName);
        return instruments.stream().map(InstrumentMapper::toDTO).collect(Collectors.toList());
    }

    public List<InstrumentDTO> getInstrumentsByExchangeType(Instrument.ExchangeType exchangeType) {
        List<Instrument> instruments = instrumentRepository.findByExchangeTypeIgnoreCase(String.valueOf(exchangeType));
        return instruments.stream().map(InstrumentMapper::toDTO).collect(Collectors.toList());
    }
}
