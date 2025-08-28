package com.edisonchavez.challenge.films.service;

import com.edisonchavez.challenge.films.dto.FilmProps;
import org.springframework.data.domain.Page;

public interface FilmService {
    Page<FilmProps> list(
            int page,
            int size,
            String search,
            String id);
    FilmProps get(String id);
}
