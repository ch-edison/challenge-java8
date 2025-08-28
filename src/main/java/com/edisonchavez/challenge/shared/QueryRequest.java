package com.edisonchavez.challenge.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryRequest {
    private Integer page;
    private Integer limit;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;
}