package com.example.StockBrokingPlatform.service;

import com.example.StockBrokingPlatform.DTO.WatchListDTO;
import com.example.StockBrokingPlatform.DTO.WatchListItemDTO;
import com.example.StockBrokingPlatform.exception.ResourceNotFoundException;
import com.example.StockBrokingPlatform.mapper.WatchListItemMapper;
import com.example.StockBrokingPlatform.mapper.WatchListMapper;
import com.example.StockBrokingPlatform.model.*;
import com.example.StockBrokingPlatform.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchListService {

    @Autowired
    private WatchListRepository watchListRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private WatchListItemRepository watchListItemRepository;

    public List<WatchListDTO> getWatchListsByClientId(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientId));
        return client.getWatchLists().stream()
                .map(WatchListMapper::toDTO)
                .collect(Collectors.toList());
    }

    public WatchListDTO getWatchListById(Long id) {
        WatchList watchList = watchListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with id " + id));
        return WatchListMapper.toDTO(watchList);
    }

    public WatchListDTO createWatchList(WatchListDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + dto.getClientId()));
        if (client.getWatchLists().size() >= 5) {
            throw new IllegalStateException("Client cannot have more than 5 watchlists");
        }
        WatchList watchList = WatchListMapper.toEntity(dto, client);
        return WatchListMapper.toDTO(watchListRepository.save(watchList));
    }

    public WatchListDTO updateWatchList(Long id, WatchListDTO dto) {
        WatchList watchList = watchListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with id " + id));
        watchList.setName(dto.getName());
        return WatchListMapper.toDTO(watchListRepository.save(watchList));
    }

    public void deleteWatchList(Long id) {
        WatchList watchList = watchListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with id " + id));

        if (watchList.isDefault()) {
            throw new IllegalStateException("Default watchlist cannot be deleted");
        }

        List<WatchList> defaultLists = watchList.getClient().getWatchLists().stream()
                .filter(wl -> !wl.getId().equals(id) && wl.isDefault())
                .toList();

        if (watchList.isDefault() && defaultLists.isEmpty()) {
            throw new IllegalStateException("At least one default watchlist must remain per client");
        }

        // 💡 Important: Clear the items list to enable orphan removal
        if (watchList.getItems() != null) {
            watchList.getItems().clear();  // triggers orphanRemoval
        }

        watchListRepository.delete(watchList);
        System.out.println("Deleted watchlist.");
    }

    public WatchListItemDTO addInstrumentToWatchList(Long watchListId, WatchListItemDTO dto) {
        WatchList watchList = watchListRepository.findById(watchListId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with id " + watchListId));
        Instrument instrument = instrumentRepository.findById(dto.getInstrumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found with id " + dto.getInstrumentId()));

        // Validation: Prevent duplicate instruments
        boolean alreadyExists = watchList.getItems().stream()
                .anyMatch(item -> item.getInstrument().getId().equals(dto.getInstrumentId()));
        if (alreadyExists) {
            throw new IllegalStateException("Instrument is already in the watchlist");
        }

        // Validation: Max 20 instruments per watchlist
        if (watchList.getItems().size() >= 20) {
            throw new IllegalStateException("Cannot add more than 20 instruments to a watchlist");
        }

        WatchListItem item = new WatchListItem();
        item.setInstrument(instrument);
        item.setWatchList(watchList);
        item.setAddedDate(dto.getAddedDate() != null ? dto.getAddedDate() : java.time.LocalDateTime.now());

        return WatchListItemMapper.toDTO(watchListItemRepository.save(item));
    }

    public void removeInstrumentFromWatchList(Long watchListId, Long instrumentId) {
        WatchListItem item = watchListItemRepository.findByWatchListIdAndInstrumentId(watchListId, instrumentId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in watchlist"));
        watchListItemRepository.delete(item);
    }

    public String getWatchListSummary(Long id) {
        WatchList watchList = watchListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with id " + id));

        double totalValue = watchList.getItems().stream()
                .map(item -> item.getInstrument().getCurrentPrice())
                .filter(price -> price != null)
                .reduce(0.0, Double::sum);

        return "Watchlist '" + watchList.getName() + "' contains " + watchList.getItems().size()
                + " instruments. Total Market Value: ₹" + totalValue;
    }

    public WatchListDTO getWatchListWithItems(Long id) {
        WatchList watchList = watchListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found with id " + id));

        WatchListDTO dto = WatchListMapper.toDTO(watchList); // only base fields
        List<WatchListItemDTO> itemDTOs = watchList.getItems()
                .stream()
                .map(WatchListItemMapper::toDTO)
                .collect(Collectors.toList());

        dto.setItems(itemDTOs); // Set items explicitly
        return dto;
    }
}
