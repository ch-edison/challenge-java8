package com.edisonchavez.challenge.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    int page;
    int size;
    int total;
    int totalPages;
    List<T> items;
}