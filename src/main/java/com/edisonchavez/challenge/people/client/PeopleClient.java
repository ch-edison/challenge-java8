package com.edisonchavez.challenge.people.client;

import com.edisonchavez.challenge.people.dto.*;
import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "starwars-people",
        url = "${starwars.base-url}",
        path = "/api/people"
)
public interface PeopleClient {
    @GetMapping
    PageResponse<PersonListProps> list();

    @GetMapping
    FilterResponse<PersonProps> listFilter(@SpringQueryMap QueryRequest query);

    @GetMapping("/{id}")
    ApiResponse<ResultData<PersonProps>> get(@PathVariable("id") String id);
}
