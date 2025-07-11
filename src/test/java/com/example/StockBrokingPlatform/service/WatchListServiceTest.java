package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.WatchListDTO;
import com.example.StockBrokingPlatform.DTO.WatchListItemDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.WatchListItemMapper;
import com.example.StockBrokingPlatform.mapper.WatchListMapper;
import com.example.StockBrokingPlatform.model.*;
import com.example.StockBrokingPlatform.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchListServiceTest {

    @InjectMocks
    private WatchListService watchListService;

    @Mock
    private WatchListRepository watchListRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private InstrumentRepository instrumentRepository;
    @Mock
    private WatchListItemRepository watchListItemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWatchListById_Success() {
        Client client = new Client();
        client.setId(101L);

        WatchList watchList = new WatchList();
        watchList.setId(1L);
        watchList.setName("Tech Stocks");
        watchList.setClient(client);

        when(watchListRepository.findById(1L)).thenReturn(Optional.of(watchList));
        WatchListDTO result = watchListService.getWatchListById(1L);
        assertEquals("Tech Stocks", result.getName());
    }

    @Test
    void testGetWatchListById_NotFound() {
        when(watchListRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            watchListService.getWatchListById(99L);
        });
    }

    @Test
    void testCreateWatchList_Success() {
        Client client = new Client();
        client.setId(1L);
        client.setWatchLists(new ArrayList<>()); // ✅ Prevent NullPointerException

        WatchListDTO dto = new WatchListDTO();
        dto.setClientId(1L);
        dto.setName("New List");

        WatchList watchList = new WatchList();
        watchList.setId(10L);
        watchList.setName("New List");
        watchList.setClient(client);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(watchListRepository.save(any(WatchList.class))).thenReturn(watchList);

        WatchListDTO result = watchListService.createWatchList(dto);

        assertEquals("New List", result.getName());
    }

    @Test
    void testAddInstrumentToWatchList_Success() {
        WatchList watchList = new WatchList();
        watchList.setId(1L);
        watchList.setItems(new ArrayList<>()); // ✅ Fix added

        Instrument instrument = new Instrument();
        instrument.setId(2L);
        instrument.setSymbol("TCS");

        WatchListItemDTO dto = new WatchListItemDTO();
        dto.setInstrumentId(2L);
        dto.setAddedDate(LocalDateTime.now());

        WatchListItem savedItem = new WatchListItem();
        savedItem.setWatchList(watchList);
        savedItem.setInstrument(instrument);
        savedItem.setAddedDate(dto.getAddedDate());

        when(watchListRepository.findById(1L)).thenReturn(Optional.of(watchList));
        when(instrumentRepository.findById(2L)).thenReturn(Optional.of(instrument));
        when(watchListItemRepository.save(any(WatchListItem.class))).thenReturn(savedItem);

        WatchListItemDTO result = watchListService.addInstrumentToWatchList(1L, dto);

        assertEquals(2L, result.getInstrumentId());
    }

    @Test
    void testRemoveInstrumentFromWatchList_Success() {
        WatchListItem item = new WatchListItem();
        item.setId(1L);

        when(watchListItemRepository.findByWatchListIdAndInstrumentId(1L, 2L))
                .thenReturn(Optional.of(item));

        watchListService.removeInstrumentFromWatchList(1L, 2L);

        verify(watchListItemRepository, times(1)).delete(item);
    }

    @Test
    void testAddInstrumentToWatchList_DuplicateInstrument_ThrowsException() {
        WatchList watchList = new WatchList();
        watchList.setId(1L);

        Instrument instrument = new Instrument();
        instrument.setId(1L);

        WatchListItem existingItem = new WatchListItem();
        existingItem.setInstrument(instrument);

        watchList.setItems(List.of(existingItem));

        WatchListItemDTO dto = new WatchListItemDTO();
        dto.setInstrumentId(1L);

        when(watchListRepository.findById(1L)).thenReturn(Optional.of(watchList));
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        assertThrows(IllegalStateException.class, () -> watchListService.addInstrumentToWatchList(1L, dto));
    }

    @Test
    void testCreateWatchList_MaxLimitReached_ThrowsException() {
        Client client = new Client();
        client.setId(1L);
        client.setWatchLists(new ArrayList<>(Collections.nCopies(5, new WatchList())));

        WatchListDTO dto = new WatchListDTO();
        dto.setClientId(1L);
        dto.setName("Extra");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThrows(IllegalStateException.class, () -> watchListService.createWatchList(dto));
    }

    @Test
    void testGetWatchListSummary() {
        Instrument i1 = new Instrument();
        i1.setCurrentPrice(100.0);
        Instrument i2 = new Instrument();
        i2.setCurrentPrice(200.0);

        WatchListItem item1 = new WatchListItem();
        item1.setInstrument(i1);
        WatchListItem item2 = new WatchListItem();
        item2.setInstrument(i2);

        WatchList watchList = new WatchList();
        watchList.setName("MyList");
        watchList.setItems(List.of(item1, item2));

        when(watchListRepository.findById(1L)).thenReturn(Optional.of(watchList));

        String summary = watchListService.getWatchListSummary(1L);

        assertTrue(summary.contains("₹300.0"));
        assertTrue(summary.contains("2 instruments"));
    }

    @Test
    void testGetWatchListWithItems() {
        Client client = new Client();
        client.setId(1L);

        Instrument i1 = new Instrument();
        i1.setId(101L);
        i1.setSymbol("INFY");

        WatchListItem item = new WatchListItem();
        item.setInstrument(i1);
        item.setAddedDate(LocalDateTime.now());

        WatchList watchList = new WatchList();
        watchList.setId(1L);
        watchList.setName("Top 50");
        watchList.setItems(List.of(item));
        watchList.setClient(client);

        when(watchListRepository.findById(1L)).thenReturn(Optional.of(watchList));

        WatchListDTO dto = watchListService.getWatchListWithItems(1L);

        assertEquals("Top 50", dto.getName());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testDeleteWatchList_DefaultWatchlist_ThrowsException() {
        WatchList watchList = new WatchList();
        watchList.setId(1L);
        watchList.setDefault(true);

        when(watchListRepository.findById(1L)).thenReturn(Optional.of(watchList));

        assertThrows(IllegalStateException.class, () -> watchListService.deleteWatchList(1L));
    }

}