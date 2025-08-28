package com.edisonchavez.challenge.films.client;

import com.edisonchavez.challenge.films.dto.*;
import com.edisonchavez.challenge.shared.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "starwars-film",
        url = "${starwars.base-url}",
        path = "/api/films"
)
public interface FilmClient {
    @GetMapping
    ApiListResponse<ResultData<FilmProps>> list();

    @GetMapping("/{id}")
    ApiResponse<ResultData<FilmProps>> get(@PathVariable("id") String id);
}
