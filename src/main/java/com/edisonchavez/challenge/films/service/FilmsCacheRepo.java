package com.edisonchavez.challenge.films.service;

import com.edisonchavez.challenge.films.client.FilmClient;
import com.edisonchavez.challenge.films.dto.ApiListResponse;
import com.edisonchavez.challenge.films.dto.FilmProps;
import com.edisonchavez.challenge.shared.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmsCacheRepo {

    private final FilmClient client;

    @Cacheable(cacheNames = "films:list", key = "'all'")
    public List<FilmProps> fetchAll() {
        ApiListResponse<ResultData<FilmProps>> resp = client.list();
        if (resp.getResult() == null) return Collections.emptyList();
        return resp.getResult().stream().map(ResultData::getProperties).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "films:detail", key = "#id")
    public FilmProps fetchById(String id) {
        return client.get(id).getResult().getProperties();
    }
}