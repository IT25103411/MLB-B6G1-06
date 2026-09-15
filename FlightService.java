package com.cloudwing.service;

import com.cloudwing.dto.*;
import com.cloudwing.entity.FlightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    List<FlightDto> searchFlights(String origin, String destination, LocalDate departureDate, BigDecimal maxPrice, Integer stops);
    Page<FlightDto> getAdminFlights(String query, FlightStatus status, Pageable pageable);
    FlightDto getFlightById(Long id);
    FlightDto getFlightByNumber(String flightNumber);
    FlightDto createFlight(FlightCreateRequest request);
    FlightDto updateFlight(Long id, FlightUpdateRequest request);
    FlightDto updateFlightStatus(Long id, FlightStatusUpdateRequest request);
    void cancelFlight(Long id);
    void deleteFlight(Long id);
}
