package com.example.StockBrokingPlatform.repository;

import com.example.StockBrokingPlatform.model.WatchListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchListItemRepository extends JpaRepository<WatchListItem, Long> {
    Optional<WatchListItem> findByWatchListIdAndInstrumentId(Long watchListId, Long instrumentId);
}
