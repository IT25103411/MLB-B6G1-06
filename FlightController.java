package com.cloudwing.controller;

import com.cloudwing.dto.*;
import com.cloudwing.entity.FlightStatus;
import com.cloudwing.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightDto>>> searchFlights(
            @RequestParam("from") String origin,
            @RequestParam("to") String destination,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "stops", required = false) Integer stops
    ) {
        List<FlightDto> flights = flightService.searchFlights(origin, destination, departureDate, maxPrice, stops);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FlightDto>>> getFlights(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "status", required = false) FlightStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "departureTime") String sortBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FlightDto> flightPage = flightService.getAdminFlights(query, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(flightPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightById(@PathVariable Long id) {
        FlightDto flight = flightService.getFlightById(id);
        return ResponseEntity.ok(ApiResponse.success(flight));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATIONS_MANAGER')")
    public ResponseEntity<ApiResponse<FlightDto>> createFlight(@Valid @RequestBody FlightCreateRequest request) {
        FlightDto createdFlight = flightService.createFlight(request);
        return new ResponseEntity<>(ApiResponse.success("Flight created successfully", createdFlight), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATIONS_MANAGER')")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightUpdateRequest request) {
        FlightDto updatedFlight = flightService.updateFlight(id, request);
        return ResponseEntity.ok(ApiResponse.success("Flight updated successfully", updatedFlight));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERATIONS_MANAGER')")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlightStatus(
            @PathVariable Long id,
            @Valid @RequestBody FlightStatusUpdateRequest request) {
        FlightDto updatedFlight = flightService.updateFlightStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Flight status updated successfully", updatedFlight));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(ApiResponse.success("Flight cancelled successfully", null));
    }
}
