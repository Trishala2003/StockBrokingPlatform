package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.InstrumentDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.InstrumentMapper;
import com.example.StockBrokingPlatform.model.Instrument;
import com.example.StockBrokingPlatform.repository.InstrumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static com.example.StockBrokingPlatform.model.Instrument.Exchange.NSE;
import static com.example.StockBrokingPlatform.model.Instrument.ExchangeType.Equity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InstrumentServiceTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @InjectMocks
    private InstrumentService instrumentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Instrument createInstrument() {
        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setSymbol("RELIANCE");
        instrument.setCompanyName("Reliance Industries");
        instrument.setExchange(NSE);
        instrument.setExchangeType(Equity);
        instrument.setCurrentPrice(2450.50);
        instrument.setLotSize(1);
        return instrument;
    }

    @Test
    void testGetAllInstruments() {
        List<Instrument> instruments = List.of(createInstrument());
        Page<Instrument> page = new PageImpl<>(instruments);

        when(instrumentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<InstrumentDTO> result = instrumentService.getAllInstruments(0, 10);
        assertEquals(1, result.size());
        verify(instrumentRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetInstrumentById_Found() {
        Instrument instrument = createInstrument();
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        InstrumentDTO result = instrumentService.getInstrumentById(1L);
        assertEquals("RELIANCE", result.getSymbol());
        verify(instrumentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetInstrumentById_NotFound() {
        when(instrumentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> instrumentService.getInstrumentById(1L));
    }

    @Test
    void testAddInstrument() {
        Instrument instrument = createInstrument();
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(instrument);

        InstrumentDTO dto = InstrumentMapper.toDTO(instrument);
        InstrumentDTO result = instrumentService.addInstrument(dto);

        assertEquals("RELIANCE", result.getSymbol());
        verify(instrumentRepository, times(1)).save(any(Instrument.class));
    }

    @Test
    void testUpdateInstrument_Success() {
        Instrument existing = createInstrument();
        InstrumentDTO updatedDto = InstrumentMapper.toDTO(existing);
        updatedDto.setCompanyName("Reliance Updated");

        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(existing);

        InstrumentDTO result = instrumentService.updateInstrument(1L, updatedDto);
        assertEquals("Reliance Updated", result.getCompanyName());
        verify(instrumentRepository).save(any(Instrument.class));
    }

    @Test
    void testUpdateInstrument_NotFound() {
        when(instrumentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> instrumentService.updateInstrument(1L, new InstrumentDTO()));
    }

    @Test
    void testSearchBySymbolOrCompanyName() {
        Instrument instrument = createInstrument();
        when(instrumentRepository.findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase("REL", "REL"))
                .thenReturn(List.of(instrument));

        List<InstrumentDTO> result = instrumentService.searchBySymbolOrCompanyName("REL", "REL");
        assertEquals(1, result.size());
    }

    @Test
    void testGetInstrumentsByExchangeType() {
        Instrument instrument = createInstrument();
        when(instrumentRepository.findByExchangeTypeIgnoreCase("Equity")).thenReturn(List.of(instrument));

        List<InstrumentDTO> result = instrumentService.getInstrumentsByExchangeType(Equity);
        assertEquals(Equity, result.get(0).getExchangeType());
    }
}

