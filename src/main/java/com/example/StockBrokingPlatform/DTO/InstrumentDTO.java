package com.example.StockBrokingPlatform.DTO;

import com.example.StockBrokingPlatform.model.Instrument;

public class InstrumentDTO {
    private Long id;
    private String symbol;
    private String companyName;
    private Instrument.Exchange exchange;
    private Instrument.ExchangeType exchangeType;
    private Double currentPrice;
    private Integer lotSize;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Instrument.Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Instrument.Exchange exchange) {
        this.exchange = exchange;
    }

    public Instrument.ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Instrument.ExchangeType exchangeType) {
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

}
