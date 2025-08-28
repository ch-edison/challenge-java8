package com.edisonchavez.challenge.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterResponse<T> {
    private String message;
    @JsonProperty("result")
    private List<ResultData<T>> result;
}