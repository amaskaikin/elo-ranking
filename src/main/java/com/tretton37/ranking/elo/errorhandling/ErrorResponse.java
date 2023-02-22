package com.tretton37.ranking.elo.errorhandling;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String code;
    private String message;
    private String reason;
    @JsonIgnore
    private ErrorDetails errorDetails;

    public ErrorResponse(ErrorDetails errorDetails, String reason) {
        this.timestamp = LocalDateTime.now();
        this.errorDetails = errorDetails;
        this.reason = reason;
    }

    public String getCode() {
        return this.errorDetails.getCode();
    }

    public String getMessage() {
        return this.errorDetails.getMessage();
    }
}