package com.edisonchavez.challenge.starships.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StarshipsListProps implements Serializable {
    private String uid;
    private String name;
    private String url;
}