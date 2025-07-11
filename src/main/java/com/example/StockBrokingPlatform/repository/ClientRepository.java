package com.example.StockBrokingPlatform.repository;

import com.example.StockBrokingPlatform.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("""
    SELECT i FROM Client i
    WHERE (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:clientCode IS NULL OR LOWER(i.clientCode) LIKE LOWER(CONCAT('%', :clientCode, '%')))
    """)
    List<Client> findByNameContainingIgnoreCaseOrClientCodeContainingIgnoreCase(String name, String clientCode);
}
