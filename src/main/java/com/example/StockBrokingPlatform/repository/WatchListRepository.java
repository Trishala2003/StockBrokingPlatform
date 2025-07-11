package com.example.StockBrokingPlatform.repository;

import com.example.StockBrokingPlatform.model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {
    List<WatchList> findByClientId(Long clientId);
}
