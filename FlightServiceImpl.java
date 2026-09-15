package com.cloudwing.service.impl;

import com.cloudwing.dto.*;
import com.cloudwing.entity.Aircraft;
import com.cloudwing.entity.Airport;
import com.cloudwing.entity.Flight;
import com.cloudwing.entity.FlightStatus;
import com.cloudwing.exception.BadRequestException;
import com.cloudwing.exception.DuplicateResourceException;
import com.cloudwing.exception.ResourceNotFoundException;
import com.cloudwing.repository.AircraftRepository;
import com.cloudwing.repository.AirportRepository;
import com.cloudwing.repository.FlightRepository;
import com.cloudwing.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository,
                             AirportRepository airportRepository,
                             AircraftRepository aircraftRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.aircraftRepository = aircraftRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> searchFlights(String origin, String destination, LocalDate departureDate,
                                         BigDecimal maxPrice, Integer stops) {
        if (origin == null || destination == null || departureDate == null) {
            throw new BadRequestException("Origin, destination, and departure date are required for flight search.");
        }

        LocalDateTime startTime = departureDate.atStartOfDay();
        LocalDateTime endTime = departureDate.atTime(LocalTime.MAX);

        List<Flight> flights = flightRepository.searchFlights(
                origin.toUpperCase(),
                destination.toUpperCase(),
                startTime,
                endTime,
                maxPrice,
                stops
        );

        return flights.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightDto> getAdminFlights(String query, FlightStatus status, Pageable pageable) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return flightRepository.findAdminFlights(cleanQuery, status, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightDto getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));
        return mapToDto(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightDto getFlightByNumber(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "flightNumber", flightNumber));
        return mapToDto(flight);
    }

    @Override
    @Transactional
    public FlightDto createFlight(FlightCreateRequest request) {
        String cleanFlightNumber = request.getFlightNumber().trim().toUpperCase();
        if (flightRepository.existsByFlightNumber(cleanFlightNumber)) {
            throw new DuplicateResourceException("A flight with number " + cleanFlightNumber + " already exists.");
        }

        if (request.getOriginAirportCode().equalsIgnoreCase(request.getDestinationAirportCode())) {
            throw new BadRequestException("Origin and destination airports cannot be identical.");
        }

        if (!request.getDepartureTime().isBefore(request.getArrivalTime())) {
            throw new BadRequestException("Departure time must be strictly before arrival time.");
        }

        Airport originAirport = airportRepository.findByIataCode(request.getOriginAirportCode().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Airport", "iataCode", request.getOriginAirportCode()));

        Airport destinationAirport = airportRepository.findByIataCode(request.getDestinationAirportCode().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Airport", "iataCode", request.getDestinationAirportCode()));

        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft", "id", request.getAircraftId()));

        Flight flight = new Flight();
        flight.setFlightNumber(cleanFlightNumber);
        flight.setOriginAirport(originAirport);
        flight.setDestinationAirport(destinationAirport);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setBasePrice(request.getBasePrice());
        flight.setBusinessPrice(request.getBusinessPrice());
        flight.setFirstPrice(request.getFirstPrice());
        flight.setAvailableEconomySeats(aircraft.getEconomyCapacity());
        flight.setAvailableBusinessSeats(aircraft.getBusinessCapacity());
        flight.setAvailableFirstSeats(aircraft.getFirstCapacity());
        flight.setStops(request.getStops());
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setCancelled(false);

        Flight savedFlight = flightRepository.save(flight);
        return mapToDto(savedFlight);
    }

    @Override
    @Transactional
    public FlightDto updateFlight(Long id, FlightUpdateRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));

        if (request.getDepartureTime() != null && request.getArrivalTime() != null) {
            if (!request.getDepartureTime().isBefore(request.getArrivalTime())) {
                throw new BadRequestException("Departure time must be strictly before arrival time.");
            }
            flight.setDepartureTime(request.getDepartureTime());
            flight.setArrivalTime(request.getArrivalTime());
        } else if (request.getDepartureTime() != null) {
            if (!request.getDepartureTime().isBefore(flight.getArrivalTime())) {
                throw new BadRequestException("Departure time must be strictly before arrival time.");
            }
            flight.setDepartureTime(request.getDepartureTime());
        } else if (request.getArrivalTime() != null) {
            if (!flight.getDepartureTime().isBefore(request.getArrivalTime())) {
                throw new BadRequestException("Departure time must be strictly before arrival time.");
            }
            flight.setArrivalTime(request.getArrivalTime());
        }

        if (request.getBasePrice() != null) {
            flight.setBasePrice(request.getBasePrice());
        }
        if (request.getBusinessPrice() != null) {
            flight.setBusinessPrice(request.getBusinessPrice());
        }
        if (request.getFirstPrice() != null) {
            flight.setFirstPrice(request.getFirstPrice());
        }
        if (request.getStatus() != null) {
            flight.setStatus(request.getStatus());
        }
        if (request.getAvailableEconomySeats() != null) {
            flight.setAvailableEconomySeats(request.getAvailableEconomySeats());
        }
        if (request.getAvailableBusinessSeats() != null) {
            flight.setAvailableBusinessSeats(request.getAvailableBusinessSeats());
        }
        if (request.getAvailableFirstSeats() != null) {
            flight.setAvailableFirstSeats(request.getAvailableFirstSeats());
        }

        Flight updatedFlight = flightRepository.save(flight);
        return mapToDto(updatedFlight);
    }

    @Override
    @Transactional
    public FlightDto updateFlightStatus(Long id, FlightStatusUpdateRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));

        flight.setStatus(request.getStatus());
        if (request.getStatus() == FlightStatus.CANCELLED) {
            flight.setCancelled(true);
        } else {
            flight.setCancelled(false);
        }

        Flight savedFlight = flightRepository.save(flight);
        return mapToDto(savedFlight);
    }

    @Override
    @Transactional
    public void cancelFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));

        flight.setStatus(FlightStatus.CANCELLED);
        flight.setCancelled(true);
        flightRepository.save(flight);
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));

        // In Phase 1 soft cancellation is preferred
        flight.setStatus(FlightStatus.CANCELLED);
        flight.setCancelled(true);
        flightRepository.save(flight);
    }

    private FlightDto mapToDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDuration(FlightDto.calculateDuration(flight.getDepartureTime(), flight.getArrivalTime()));
        dto.setBasePrice(flight.getBasePrice());
        dto.setBusinessPrice(flight.getBusinessPrice());
        dto.setFirstPrice(flight.getFirstPrice());
        dto.setAvailableEconomySeats(flight.getAvailableEconomySeats());
        dto.setAvailableBusinessSeats(flight.getAvailableBusinessSeats());
        dto.setAvailableFirstSeats(flight.getAvailableFirstSeats());
        dto.setStops(flight.getStops());
        dto.setStatus(flight.getStatus());
        dto.setCancelled(flight.isCancelled());
        dto.setBookingsCount(0);

        if (flight.getOriginAirport() != null) {
            dto.setOriginAirport(new AirportDto(
                    flight.getOriginAirport().getId(),
                    flight.getOriginAirport().getIataCode(),
                    flight.getOriginAirport().getName(),
                    flight.getOriginAirport().getCity(),
                    flight.getOriginAirport().getCountry(),
                    flight.getOriginAirport().getTimezone(),
                    flight.getOriginAirport().isActive()
            ));
        }

        if (flight.getDestinationAirport() != null) {
            dto.setDestinationAirport(new AirportDto(
                    flight.getDestinationAirport().getId(),
                    flight.getDestinationAirport().getIataCode(),
                    flight.getDestinationAirport().getName(),
                    flight.getDestinationAirport().getCity(),
                    flight.getDestinationAirport().getCountry(),
                    flight.getDestinationAirport().getTimezone(),
                    flight.getDestinationAirport().isActive()
            ));
        }

        if (flight.getAircraft() != null) {
            dto.setAircraft(new AircraftDto(
                    flight.getAircraft().getId(),
                    flight.getAircraft().getModel(),
                    flight.getAircraft().getTailNumber(),
                    flight.getAircraft().getEconomyCapacity(),
                    flight.getAircraft().getBusinessCapacity(),
                    flight.getAircraft().getFirstCapacity(),
                    flight.getAircraft().getTotalCapacity(),
                    flight.getAircraft().isActive()
            ));
        }

        return dto;
    }
}
