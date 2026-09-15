package com.cloudwing.dto;

import com.cloudwing.entity.FlightStatus;
import jakarta.validation.constraints.NotNull;

public class FlightStatusUpdateRequest {

    @NotNull(message = "Flight status is required")
    private FlightStatus status;

    public FlightStatusUpdateRequest() {
    }

    public FlightStatusUpdateRequest(FlightStatus status) {
        this.status = status;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
}
