package com.example.StockBrokingPlatform.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instrument")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String companyName;

    public enum Exchange {
        NSE, BSE, MCX
    }
    @Enumerated(EnumType.STRING)
    private Exchange exchange;

    public enum ExchangeType {
        Equity, Futures, Options, Currency, Commodity
    }
    @Enumerated(EnumType.STRING)
    private ExchangeType exchangeType;

    private Double currentPrice;
    private Integer lotSize;

    @OneToMany(mappedBy = "instrument")
    private List<WatchListItem> watchListItems = new ArrayList<>();

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Exchange getExchange() { return exchange;}

    public void setExchange(Exchange exchange) {this.exchange = exchange;}

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchange(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public List<WatchListItem> getWatchListItems() {
        return watchListItems;
    }

    public void setWatchListItems(List<WatchListItem> watchListItems) {
        this.watchListItems = watchListItems;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
