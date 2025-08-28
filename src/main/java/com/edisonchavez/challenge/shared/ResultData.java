package com.edisonchavez.challenge.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultData<T> {
    @JsonProperty("properties")
    private T properties;
    private String description;
    private String uid;
    @JsonProperty("_id")
    private String id;
}