package com.cloudwing.repository;

import com.cloudwing.entity.FlightSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightSeatRepository extends JpaRepository<FlightSeat, Long> {
    List<FlightSeat> findByFlightId(Long flightId);
    Optional<FlightSeat> findByFlightIdAndSeatNumber(Long flightId, String seatNumber);
}
