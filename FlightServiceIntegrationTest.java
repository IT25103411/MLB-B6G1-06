package com.cloudwing.service;

import com.cloudwing.dto.*;
import com.cloudwing.entity.*;
import com.cloudwing.exception.BadRequestException;
import com.cloudwing.repository.AircraftRepository;
import com.cloudwing.repository.AirportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FlightServiceIntegrationTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    private Long aircraftId;

    @BeforeEach
    void setUp() {
        if (airportRepository.findByIataCode("JFK").isEmpty()) {
            airportRepository.save(new Airport("JFK", "John F Kennedy", "New York", "USA", "America/New_York"));
        }
        if (airportRepository.findByIataCode("LHR").isEmpty()) {
            airportRepository.save(new Airport("LHR", "Heathrow", "London", "UK", "Europe/London"));
        }
        Aircraft aircraft = aircraftRepository.findByTailNumber("CW-TEST-AIRCRAFT")
                .orElseGet(() -> aircraftRepository.save(new Aircraft("A350-1000", "CW-TEST-AIRCRAFT", 280, 46, 12)));
        aircraftId = aircraft.getId();
    }

    @Test
    @DisplayName("Should create flight with valid parameters and verify fields")
    void shouldCreateFlight() {
        FlightCreateRequest request = new FlightCreateRequest();
        request.setFlightNumber("CW888");
        request.setOriginAirportCode("JFK");
        request.setDestinationAirportCode("LHR");
        request.setAircraftId(aircraftId);
        request.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        request.setArrivalTime(LocalDateTime.now().plusDays(2).withHour(22).withMinute(30));
        request.setBasePrice(new BigDecimal("520.00"));
        request.setBusinessPrice(new BigDecimal("2100.00"));
        request.setStops(0);

        FlightDto created = flightService.createFlight(request);

        assertThat(created).isNotNull();
        assertThat(created.getFlightNumber()).isEqualTo("CW888");
        assertThat(created.getOriginAirport().getIataCode()).isEqualTo("JFK");
        assertThat(created.getDestinationAirport().getIataCode()).isEqualTo("LHR");
        assertThat(created.getAvailableEconomySeats()).isEqualTo(280);
        assertThat(created.getStatus()).isEqualTo(FlightStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Should reject flight creation when origin and destination are identical")
    void shouldRejectSameOriginDestination() {
        FlightCreateRequest request = new FlightCreateRequest();
        request.setFlightNumber("CW777");
        request.setOriginAirportCode("JFK");
        request.setDestinationAirportCode("JFK");
        request.setAircraftId(aircraftId);
        request.setDepartureTime(LocalDateTime.now().plusDays(2));
        request.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(4));
        request.setBasePrice(new BigDecimal("300.00"));
        request.setBusinessPrice(new BigDecimal("1000.00"));

        assertThatThrownBy(() -> flightService.createFlight(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("identical");
    }

    @Test
    @DisplayName("Should soft-cancel flight and update status")
    void shouldSoftCancelFlight() {
        FlightCreateRequest request = new FlightCreateRequest();
        request.setFlightNumber("CW666");
        request.setOriginAirportCode("JFK");
        request.setDestinationAirportCode("LHR");
        request.setAircraftId(aircraftId);
        request.setDepartureTime(LocalDateTime.now().plusDays(3).withHour(8).withMinute(0));
        request.setArrivalTime(LocalDateTime.now().plusDays(3).withHour(20).withMinute(0));
        request.setBasePrice(new BigDecimal("450.00"));
        request.setBusinessPrice(new BigDecimal("1800.00"));

        FlightDto created = flightService.createFlight(request);
        flightService.cancelFlight(created.getId());

        FlightDto cancelled = flightService.getFlightById(created.getId());
        assertThat(cancelled.getStatus()).isEqualTo(FlightStatus.CANCELLED);
        assertThat(cancelled.isCancelled()).isTrue();
    }
}
