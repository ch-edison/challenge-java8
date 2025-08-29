package com.edisonchavez.challenge.starships.client;

import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import com.edisonchavez.challenge.starships.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "starwars-starships",
        url = "${starwars.base-url}",
        path = "/api/starships"
)
public interface StarshipsClient {
    @GetMapping()
    PageResponse<StarshipsListProps> list();

    @GetMapping
    FilterResponse<StarshipsProps> listFilter(@SpringQueryMap QueryRequest query);

    @GetMapping("/{id}")
    ApiResponse<ResultData<StarshipsProps>> get(@PathVariable("id") String id);

}
