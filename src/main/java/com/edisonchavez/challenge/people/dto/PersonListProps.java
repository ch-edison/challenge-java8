package com.edisonchavez.challenge.people.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonListProps implements Serializable {
    private String uid;
    private String name;
    private String url;
}