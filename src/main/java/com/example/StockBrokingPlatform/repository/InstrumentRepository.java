package com.example.StockBrokingPlatform.repository;

import com.example.StockBrokingPlatform.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long>{

    @Query("SELECT i FROM Instrument i WHERE " +
            "(:symbol IS NULL OR LOWER(i.symbol) LIKE LOWER(CONCAT('%', :symbol, '%'))) AND " +
            "(:companyName IS NULL OR LOWER(i.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))")
    List<Instrument> findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(@Param("symbol") String symbol, @Param("companyName") String companyName);

    @Query("SELECT i FROM Instrument i WHERE"+
            "(:exchangeType IS NULL OR LOWER(i.exchangeType) LIKE LOWER(CONCAT('%', :exchangeType, '%')))")
    List<Instrument> findByExchangeTypeIgnoreCase(String exchangeType);
}

