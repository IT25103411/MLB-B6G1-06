package com.cloudwing.dto;

import com.cloudwing.entity.FlightStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class FlightDto {

    private Long id;
    private String flightNumber;
    private AirportDto originAirport;
    private AirportDto destinationAirport;
    private AircraftDto aircraft;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private BigDecimal basePrice;
    private BigDecimal businessPrice;
    private BigDecimal firstPrice;
    private int availableEconomySeats;
    private int availableBusinessSeats;
    private int availableFirstSeats;
    private int stops;
    private FlightStatus status;
    private boolean cancelled;
    private int bookingsCount;

    public FlightDto() {
    }

    public static String calculateDuration(LocalDateTime dep, LocalDateTime arr) {
        if (dep == null || arr == null) return "0h 00m";
        Duration duration = Duration.between(dep, arr);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh %02dm", hours, minutes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public AirportDto getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(AirportDto originAirport) {
        this.originAirport = originAirport;
    }

    public AirportDto getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(AirportDto destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public AircraftDto getAircraft() {
        return aircraft;
    }

    public void setAircraft(AircraftDto aircraft) {
        this.aircraft = aircraft;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public int getAvailableEconomySeats() {
        return availableEconomySeats;
    }

    public void setAvailableEconomySeats(int availableEconomySeats) {
        this.availableEconomySeats = availableEconomySeats;
    }

    public int getAvailableBusinessSeats() {
        return availableBusinessSeats;
    }

    public void setAvailableBusinessSeats(int availableBusinessSeats) {
        this.availableBusinessSeats = availableBusinessSeats;
    }

    public int getAvailableFirstSeats() {
        return availableFirstSeats;
    }

    public void setAvailableFirstSeats(int availableFirstSeats) {
        this.availableFirstSeats = availableFirstSeats;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getBookingsCount() {
        return bookingsCount;
    }

    public void setBookingsCount(int bookingsCount) {
        this.bookingsCount = bookingsCount;
    }
}
