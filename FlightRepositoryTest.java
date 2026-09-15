package com.cloudwing.repository;

import com.cloudwing.entity.*;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    private Airport jfk;
    private Airport lhr;
    private Aircraft a350;

    @BeforeEach
    void setUp() {
        jfk = airportRepository.findByIataCode("JFK")
                .orElseGet(() -> airportRepository.save(new Airport("JFK", "John F Kennedy", "New York", "USA", "America/New_York")));
        lhr = airportRepository.findByIataCode("LHR")
                .orElseGet(() -> airportRepository.save(new Airport("LHR", "Heathrow", "London", "UK", "Europe/London")));
        a350 = aircraftRepository.findByTailNumber("CW-TEST-REPO")
                .orElseGet(() -> aircraftRepository.save(new Aircraft("A350-1000", "CW-TEST-REPO", 280, 46, 12)));
    }

    @Test
    @DisplayName("Should persist flight and search by origin, destination, and departure date")
    void shouldPersistAndSearchFlights() {
        LocalDateTime departure = LocalDate.now().plusDays(10).atTime(8, 30);
        LocalDateTime arrival = LocalDate.now().plusDays(10).atTime(20, 45);

        Flight flight = new Flight();
        flight.setFlightNumber("CW9999");
        flight.setOriginAirport(jfk);
        flight.setDestinationAirport(lhr);
        flight.setAircraft(a350);
        flight.setDepartureTime(departure);
        flight.setArrivalTime(arrival);
        flight.setBasePrice(new BigDecimal("550.00"));
        flight.setBusinessPrice(new BigDecimal("2100.00"));
        flight.setAvailableEconomySeats(250);
        flight.setAvailableBusinessSeats(40);
        flight.setAvailableFirstSeats(10);
        flight.setStops(0);
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setCancelled(false);

        flightRepository.save(flight);

        Optional<Flight> found = flightRepository.findByFlightNumber("CW9999");
        assertThat(found).isPresent();
        assertThat(found.get().getBasePrice()).isEqualByComparingTo("550.00");

        LocalDate searchDate = LocalDate.now().plusDays(10);
        List<Flight> searchResults = flightRepository.searchFlights(
                "JFK", "LHR",
                searchDate.atStartOfDay(),
                searchDate.atTime(LocalTime.MAX),
                new BigDecimal("600.00"),
                0
        );

        assertThat(searchResults).isNotEmpty();
        assertThat(searchResults.stream().anyMatch(f -> f.getFlightNumber().equals("CW9999"))).isTrue();
    }
}
