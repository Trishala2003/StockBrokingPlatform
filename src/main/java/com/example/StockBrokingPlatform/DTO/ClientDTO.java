package com.example.StockBrokingPlatform.DTO;


import com.example.StockBrokingPlatform.model.Client;

public class ClientDTO {
    private Long id;
    private String clientCode;
    private String name;
    private String email;
    private String phone;
    private String pan;
    private Client.KYCStatus kycStatus;
    private Client.ClientStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public Client.KYCStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(Client.KYCStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public Client.ClientStatus getStatus() {
        return status;
    }

    public void setStatus(Client.ClientStatus status) {
        this.status = status;
    }
}
