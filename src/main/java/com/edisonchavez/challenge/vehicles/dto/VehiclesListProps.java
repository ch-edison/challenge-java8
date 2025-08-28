package com.edisonchavez.challenge.vehicles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclesListProps implements Serializable {
    private String uid;
    private String name;
    private String url;
}