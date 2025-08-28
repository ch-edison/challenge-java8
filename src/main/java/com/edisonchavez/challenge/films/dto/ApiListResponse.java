package com.edisonchavez.challenge.films.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiListResponse<T> {
    private String message;
    private List<T> result;
}