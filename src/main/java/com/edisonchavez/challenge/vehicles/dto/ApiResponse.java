package com.edisonchavez.challenge.vehicles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private String message;
    private T result;
}