package com.cloudwing.dto;

import com.cloudwing.entity.FlightStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightUpdateRequest {

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.01", message = "Business price must be greater than 0")
    private BigDecimal businessPrice;

    private BigDecimal firstPrice;
    private FlightStatus status;
    private Integer availableEconomySeats;
    private Integer availableBusinessSeats;
    private Integer availableFirstSeats;

    public FlightUpdateRequest() {
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(BigDecimal businessPrice) {
        this.businessPrice = businessPrice;
    }

    public BigDecimal getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(BigDecimal firstPrice) {
        this.firstPrice = firstPrice;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Integer getAvailableEconomySeats() {
        return availableEconomySeats;
    }

    public void setAvailableEconomySeats(Integer availableEconomySeats) {
        this.availableEconomySeats = availableEconomySeats;
    }

    public Integer getAvailableBusinessSeats() {
        return availableBusinessSeats;
    }

    public void setAvailableBusinessSeats(Integer availableBusinessSeats) {
        this.availableBusinessSeats = availableBusinessSeats;
    }

    public Integer getAvailableFirstSeats() {
        return availableFirstSeats;
    }

    public void setAvailableFirstSeats(Integer availableFirstSeats) {
        this.availableFirstSeats = availableFirstSeats;
    }
}
