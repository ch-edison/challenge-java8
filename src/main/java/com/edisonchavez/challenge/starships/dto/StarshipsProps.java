package com.edisonchavez.challenge.starships.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StarshipsProps implements Serializable {
    private String created;
    private String edited;
    private String name;
    private String model;
    private String manufacturer;
    private String costInCredits;
    private String length;
    private String maxAtmospheringSpeed;
    private String crew;
    private String passengers;
    private String cargoCapacity;
    private String consumables;
    @JsonProperty("MGLT")
    private String mglt;
    private String hyperdriveRating;
    private String starshipClass;
    private List<String> pilots;
    private List<String> films;
    private String url;
}