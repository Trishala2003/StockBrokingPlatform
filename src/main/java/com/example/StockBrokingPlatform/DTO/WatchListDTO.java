package com.example.StockBrokingPlatform.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class WatchListDTO {
    private Long id;
    private Long clientId;
    private String name;
    private boolean isDefault;
    private LocalDateTime createdDate;
    private List<WatchListItemDTO> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<WatchListItemDTO> getItems() {
        return items;
    }

    public void setItems(List<WatchListItemDTO> items) {
        this.items = items;
    }
}
