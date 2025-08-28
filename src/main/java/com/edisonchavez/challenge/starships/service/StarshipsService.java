package com.edisonchavez.challenge.starships.service;

import com.edisonchavez.challenge.starships.dto.StarshipsListProps;
import com.edisonchavez.challenge.starships.dto.StarshipsProps;
import org.springframework.data.domain.Page;

public interface StarshipsService {
    Page<StarshipsListProps> list(int page, int size, String name);
    StarshipsProps get(String id);
}
