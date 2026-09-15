package com.cloudwing.repository;

import com.cloudwing.entity.Flight;
import com.cloudwing.entity.FlightStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE " +
           "f.cancelled = false AND " +
           "f.originAirport.iataCode = :origin AND " +
           "f.destinationAirport.iataCode = :destination AND " +
           "f.departureTime >= :startTime AND f.departureTime <= :endTime AND " +
           "(:maxPrice IS NULL OR f.basePrice <= :maxPrice) AND " +
           "(:stops IS NULL OR f.stops = :stops) AND " +
           "f.availableEconomySeats > 0 " +
           "ORDER BY f.departureTime ASC")
    List<Flight> searchFlights(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("stops") Integer stops
    );

    @Query("SELECT f FROM Flight f WHERE " +
           "(:query IS NULL OR LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.originAirport.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.destinationAirport.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.originAirport.iataCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.destinationAirport.iataCode) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR f.status = :status)")
    Page<Flight> findAdminFlights(
            @Param("query") String query,
            @Param("status") FlightStatus status,
            Pageable pageable
    );
}
