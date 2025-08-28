package com.edisonchavez.challenge.films.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilmProps implements Serializable {
    private String created;
    private String edited;
    private String title;
    private Integer episodeId;
    private String director;
    private String producer;
    private String releaseDate;
    private String openingCrawl;
    private List<String> characters;
    private List<String> species;
    private List<String> planets;
    private List<String> starships;
    private List<String> vehicles;

    private String url;
}