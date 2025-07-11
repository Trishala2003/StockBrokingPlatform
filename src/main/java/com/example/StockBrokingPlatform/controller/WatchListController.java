package com.example.StockBrokingPlatform.controller;

import com.example.StockBrokingPlatform.DTO.WatchListDTO;
import com.example.StockBrokingPlatform.DTO.WatchListItemDTO;
import com.example.StockBrokingPlatform.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
public class WatchListController {

    @Autowired
    private WatchListService watchListService;

    //Get client's watch lists
    @GetMapping("/client/{clientId}")
    public List<WatchListDTO> getClientWatchlists(@PathVariable Long clientId) {
        return watchListService.getWatchListsByClientId(clientId);
    }

    //watchlists with instruments
    @GetMapping("/{id}/with-items")
    public WatchListDTO getWatchListWithItems(@PathVariable Long id) {
        return watchListService.getWatchListWithItems(id);
    }

    //Fetch WatchList
    @GetMapping("/{id}")
    public WatchListDTO getWatchListById(@PathVariable Long id) {
        return watchListService.getWatchListById(id);
    }

    //Add new
    @PostMapping
    public WatchListDTO createWatchList(@RequestBody WatchListDTO dto) {
        return watchListService.createWatchList(dto);
    }


    @PutMapping("/{id}")
    public WatchListDTO updateWatchList(@PathVariable Long id, @RequestBody WatchListDTO dto) {
        return watchListService.updateWatchList(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWatchList(@PathVariable Long id) {
        watchListService.deleteWatchList(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/instruments")
    public WatchListItemDTO addInstrument(@PathVariable Long id, @RequestBody WatchListItemDTO dto) {
        return watchListService.addInstrumentToWatchList(id, dto);
    }

    @DeleteMapping("/{id}/instruments/{instrumentId}")
    public ResponseEntity<?> removeInstrument(@PathVariable Long id, @PathVariable Long instrumentId) {
        watchListService.removeInstrumentFromWatchList(id, instrumentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/summary")
    public String getWatchListSummary(@PathVariable Long id) {
        return watchListService.getWatchListSummary(id);
    }
}
